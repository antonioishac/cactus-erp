import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import {
  App,
  Breadcrumb,
  Button,
  Card,
  Col,
  DatePicker,
  Flex,
  Form,
  Input,
  Row,
  Select,
  Space,
  Switch,
  Tooltip,
  Upload,
} from 'antd'
import { ArrowLeftOutlined, InboxOutlined, SaveOutlined } from '@ant-design/icons'
import dayjs, { type Dayjs } from 'dayjs'
import { useAuth } from '@features/auth/useAuth'
import { hasAuthority } from '@features/auth/authority'
import { useCriarDocumento, useTodasPastas, useTodosTiposDocumento } from './queries'
import type { DocumentoRequest } from './types'

const { Dragger } = Upload

const VALORES_PADRAO = { permiteExclusao: true, permiteAlteracao: true, assinado: false }

/**
 * Único endpoint de criação (`POST multipart/form-data`: metadados + arquivo)
 * — não existe `PUT` de metadado pra Documento (ver Javadoc de
 * `DocumentoController`), então esta página só serve pra criar. Editar
 * metadado depois de criado não é possível pela API; o que existe é enviar
 * uma **nova versão** (arquivo novo), na tela de detalhe do documento.
 */
export default function DocumentoUploadPage() {
  const navigate = useNavigate()
  const { message } = App.useApp()
  const [form] = Form.useForm<Record<string, unknown>>()
  const { authorities } = useAuth()
  const podeGerenciar = hasAuthority(authorities, 'GED_GERENCIAR')

  const { data: pastas, isLoading: carregandoPastas } = useTodasPastas()
  const { data: tipos, isLoading: carregandoTipos } = useTodosTiposDocumento()
  const criar = useCriarDocumento()

  const [arquivo, setArquivo] = useState<File | undefined>(undefined)

  async function handleSalvar() {
    const valores = await form.validateFields()
    if (!arquivo) {
      message.error('Selecione um arquivo.')
      return
    }
    const dataFimVigencia = valores.dataFimVigencia as Dayjs | undefined
    const metadados: DocumentoRequest = {
      ...valores,
      dataFimVigencia: dataFimVigencia ? dataFimVigencia.format('YYYY-MM-DD') : undefined,
    } as DocumentoRequest

    try {
      const criado = await criar.mutateAsync({ metadados, arquivo })
      message.success('Documento enviado com sucesso.')
      navigate(`/ged/documentos/${criado.id}`, { replace: true })
    } catch (erro) {
      message.error(erro instanceof Error ? erro.message : 'Falha ao enviar documento.')
    }
  }

  return (
    <Flex vertical gap={16}>
      <Breadcrumb
        items={[{ title: <a onClick={() => navigate('/ged/documentos')}>Documentos</a> }, { title: 'Novo documento' }]}
      />

      <Card>
        <Form form={form} layout="vertical" initialValues={VALORES_PADRAO} disabled={!podeGerenciar}>
          <Flex justify="flex-end" wrap="wrap" gap={16} style={{ marginBottom: 24 }}>
            <Space size="large">
              <Button icon={<ArrowLeftOutlined />} onClick={() => navigate('/ged/documentos')}>
                Cancelar
              </Button>
              <Tooltip title={podeGerenciar ? undefined : 'Você não tem permissão para gerenciar documentos'}>
                <Button type="primary" icon={<SaveOutlined />} loading={criar.isPending} disabled={!podeGerenciar} onClick={handleSalvar}>
                  Enviar
                </Button>
              </Tooltip>
            </Space>
          </Flex>

          <Row gutter={12}>
            <Col span={12}>
              <Form.Item name="nome" label="Nome" rules={[{ required: true, message: 'Informe o nome' }, { max: 150 }]}>
                <Input />
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item
                name="tipoDocumentoId"
                label="Tipo de documento"
                rules={[{ required: true, message: 'Selecione o tipo' }]}
                tooltip="Define o tamanho máximo de arquivo aceito."
              >
                <Select showSearch loading={carregandoTipos} optionFilterProp="label" options={tipos?.map((t) => ({ value: t.id, label: `${t.nome} (até ${t.tamanhoMaximoMb} MB)` }))} />
              </Form.Item>
            </Col>
          </Row>

          <Row gutter={12}>
            <Col span={12}>
              <Form.Item name="pastaId" label="Pasta" tooltip="Deixe em branco pra ficar na raiz.">
                <Select allowClear showSearch loading={carregandoPastas} optionFilterProp="label" options={pastas?.map((p) => ({ value: p.id, label: p.nome }))} />
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item name="dataFimVigencia" label="Fim de vigência" tooltip="Opcional — pra documentos com validade (contratos, certidões...).">
                <DatePicker style={{ width: '100%' }} format="DD/MM/YYYY" disabledDate={(d) => d.isBefore(dayjs(), 'day')} />
              </Form.Item>
            </Col>
          </Row>

          <Form.Item name="descricao" label="Descrição" rules={[{ max: 250 }]}>
            <Input />
          </Form.Item>
          <Form.Item name="palavrasChave" label="Palavras-chave" rules={[{ max: 250 }]} tooltip="Separadas por vírgula — ajuda numa futura busca.">
            <Input />
          </Form.Item>

          <Row gutter={12}>
            <Col span={8}>
              <Form.Item name="entidadeTipo" label="Vínculo — tipo de entidade" rules={[{ max: 50 }]} tooltip="Opcional — ex: PRODUTO, FORNECEDOR, quando o documento pertence a outro cadastro do sistema.">
                <Input placeholder="Ex: PRODUTO" />
              </Form.Item>
            </Col>
            <Col span={8}>
              <Form.Item name="entidadeId" label="Vínculo — id da entidade">
                <Input type="number" />
              </Form.Item>
            </Col>
          </Row>

          <Row gutter={12}>
            <Col span={8}>
              <Form.Item name="permiteExclusao" label="Permite exclusão" valuePropName="checked">
                <Switch />
              </Form.Item>
            </Col>
            <Col span={8}>
              <Form.Item name="permiteAlteracao" label="Permite nova versão" valuePropName="checked">
                <Switch />
              </Form.Item>
            </Col>
            <Col span={8}>
              <Form.Item name="assinado" label="Assinado" valuePropName="checked">
                <Switch />
              </Form.Item>
            </Col>
          </Row>

          <Form.Item label="Arquivo" required>
            <Dragger
              multiple={false}
              maxCount={1}
              beforeUpload={(file) => {
                setArquivo(file)
                return false
              }}
              onRemove={() => setArquivo(undefined)}
              fileList={arquivo ? [{ uid: '1', name: arquivo.name, size: arquivo.size, status: 'done' as const }] : []}
            >
              <p className="ant-upload-drag-icon">
                <InboxOutlined />
              </p>
              <p className="ant-upload-text">Clique ou arraste o arquivo aqui</p>
            </Dragger>
          </Form.Item>
        </Form>
      </Card>
    </Flex>
  )
}
