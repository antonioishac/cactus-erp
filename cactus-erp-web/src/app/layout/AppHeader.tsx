import { Flex, Layout } from 'antd'
import CompanySwitcher from './CompanySwitcher'
import UserMenu from './UserMenu'

const { Header } = Layout

export default function AppHeader() {
  return (
    <Header
      style={{
        background: '#fff',
        padding: '0 24px',
        borderBottom: '1px solid #f0f0f0',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'flex-end',
      }}
    >
      <Flex align="center" gap={16}>
        <CompanySwitcher />
        <UserMenu />
      </Flex>
    </Header>
  )
}
