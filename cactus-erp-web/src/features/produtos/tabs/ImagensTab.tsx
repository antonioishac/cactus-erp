import { Button, Empty, Flex, Image, Popconfirm, Space, Table, Tag } from 'antd'
import { DeleteOutlined, EditOutlined, PlusOutlined } from '@ant-design/icons'
import ImagemModal from '../modals/ImagemModal'
import { useEditableList } from '@shared/hooks/useEditableList'
import type { ProdutoImagemDTO } from '../types'

interface ImagensTabProps {
  imagens: ProdutoImagemDTO[]
  onChange: (imagens: ProdutoImagemDTO[]) => void
  somenteLeitura?: boolean
}

/** Só URL já hospedada — upload direto de arquivo depende do futuro GED (ver CLAUDE.md raiz). */
export default function ImagensTab({ imagens, onChange, somenteLeitura }: ImagensTabProps) {
  const lista = useEditableList(imagens, onChange)

  return (
    <Flex vertical gap={16}>
      {!somenteLeitura && (
        <Flex justify="flex-end">
          <Button type="primary" icon={<PlusOutlined />} onClick={lista.abrirNovo}>
            Adicionar imagem
          </Button>
        </Flex>
      )}

      <Table<ProdutoImagemDTO>
        rowKey={(imagem) => imagens.indexOf(imagem)}
        dataSource={imagens}
        pagination={false}
        locale={{ emptyText: <Empty description="Nenhuma imagem cadastrada" image={Empty.PRESENTED_IMAGE_SIMPLE} /> }}
        columns={[
          {
            title: 'Prévia',
            key: 'preview',
            width: 80,
            render: (_, img) => <Image src={img.url} width={48} height={48} style={{ objectFit: 'cover' }} fallback="" />,
          },
          { title: 'URL', dataIndex: 'url', ellipsis: true },
          { title: 'Ordem', dataIndex: 'ordem', width: 80, render: (v) => v ?? '—' },
          {
            title: 'Principal',
            dataIndex: 'principal',
            width: 100,
            render: (v) => (v ? <Tag color="success">Principal</Tag> : null),
          },
          {
            title: 'Situação',
            dataIndex: 'situacao',
            width: 100,
            render: (v) => <Tag color={v === 'ATIVO' ? 'success' : 'default'}>{v === 'ATIVO' ? 'Ativo' : 'Inativo'}</Tag>,
          },
          ...(somenteLeitura
            ? []
            : [
                {
                  title: 'Ações',
                  key: 'acoes',
                  width: 100,
                  render: (_: unknown, __: ProdutoImagemDTO, index: number) => (
                    <Space>
                      <Button type="text" icon={<EditOutlined />} onClick={() => lista.abrirEdicao(index)} />
                      <Popconfirm title="Remover esta imagem?" onConfirm={() => lista.remover(index)}>
                        <Button type="text" danger icon={<DeleteOutlined />} />
                      </Popconfirm>
                    </Space>
                  ),
                },
              ]),
        ]}
      />

      {!somenteLeitura && (
        <ImagemModal open={lista.modalAberto} valorInicial={lista.valorEdicao} onCancel={lista.fechar} onSalvar={lista.salvar} />
      )}
    </Flex>
  )
}
