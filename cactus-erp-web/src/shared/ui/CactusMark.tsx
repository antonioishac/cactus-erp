// Coordenadas medidas por varredura de pixel em cactus.jpg (274×278px original),
// numa PASSADA ÚNICA sobre a imagem original (não encadeando medidas de recortes
// anteriores — foi isso que causou o descentramento da tentativa anterior: o
// centro do círculo verdadeiro, medido pela linha mais larga/equador, é
// (148.5, 99) com raio 94.5, mas o texto "CACTUS Digital" começa perto o
// suficiente do fundo do círculo (~y 170) que não dá pra mostrar o raio inteiro
// sem pegar texto também. Em vez de um recorte não-centralizado (o que deixava
// sobra de branco de um lado e o verde grudado no outro quando recortado em
// círculo), o raio de exibição foi reduzido pra 78px — cabe no espaço disponível
// abaixo do centro (99 até ~167, limite seguro) — e RECENTRALIZADO no círculo
// verdadeiro nesse raio menor (centro Y 89, não 99): perde uma fatia pequena do
// topo do braço do cacto, mas garante o disco de recorte inteiro dentro do
// círculo verde em todas as direções — sem vazar branco, sem cortar rente.
const SOURCE_WIDTH = 274
const SOURCE_HEIGHT = 278
const CROP_SIZE = 156
const CROP_X = 71
const CROP_Y = 11

interface CactusMarkProps {
  size?: number
}

/**
 * Marca da Cactus Digital pra uso em espaços pequenos (sidebar do dashboard,
 * possivelmente favicon no futuro) — usa a FOTO REAL (`cactus-logo.jpg`), não
 * mais uma marca desenhada à mão (essa era a versão anterior, `CactusMark`
 * também, antes desta passada — o usuário pediu explicitamente pra voltar a
 * usar a logo original).
 * <p>
 * Terceira técnica tentada pra isolar só o círculo (sem o texto) — as duas
 * primeiras (recorte raster pré-processado + máscara de canal alfa via sharp,
 * passadas #4/#7 registradas no CLAUDE.md raiz) deixavam borda áspera quando
 * ampliadas. Esta usa `background-image` + `background-size`/`background-position`
 * calculados a partir do tamanho de exibição — o RECORTE acontece ao vivo no
 * navegador (que faz downscale de alta qualidade da imagem original, sem o
 * artefato de pré-processar um recorte pequeno e reamostrar depois), a foto
 * nunca precisa ser fisicamente recortada em arquivo nenhum. `border-radius:
 * 50%` clipa em círculo, e um `boxShadow` faz o contorno branco (não `border`,
 * que ocupraria espaço do layout mesmo com `box-sizing: border-box`).
 */
export default function CactusMark({ size = 34 }: CactusMarkProps) {
  const scale = size / CROP_SIZE

  return (
    <div
      role="img"
      aria-label="Cactus Digital"
      style={{
        width: size,
        height: size,
        borderRadius: '50%',
        flexShrink: 0,
        backgroundImage: 'url(/cactus-logo.jpg)',
        backgroundSize: `${SOURCE_WIDTH * scale}px ${SOURCE_HEIGHT * scale}px`,
        backgroundPosition: `${-CROP_X * scale}px ${-CROP_Y * scale}px`,
        boxShadow: '0 0 0 1.5px rgba(255,255,255,0.9), 0 2px 6px rgba(0,0,0,0.35)',
      }}
    />
  )
}
