import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import './index.css'
import App from './App.tsx'

/**
 * `form.validateFields()` do Ant Design REJEITA a promise quando algum campo é inválido (com
 * `{ errorFields, values, outOfDate }`) — e o app inteiro usa `validateFields().then(...)` sem
 * `.catch`, então toda submissão inválida virava um "unhandled rejection" no console (achado
 * testando o MDF-e num navegador real). A mensagem de erro do campo já aparece na tela, então
 * não há nada a fazer com essa rejeição: silenciamos só ela, pelo formato. Qualquer outra
 * rejeição não tratada continua aparecendo normalmente.
 */
window.addEventListener('unhandledrejection', (evento) => {
  const motivo = evento.reason as { errorFields?: unknown } | undefined
  if (motivo && Array.isArray(motivo.errorFields)) {
    evento.preventDefault()
  }
})

createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <App />
  </StrictMode>,
)
