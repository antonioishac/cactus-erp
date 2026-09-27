import { Avatar, Dropdown, Flex, Typography } from 'antd'
import { LogoutOutlined, UserOutlined } from '@ant-design/icons'
import { useAuth } from '@features/auth/useAuth'

const { Text } = Typography

export default function UserMenu() {
  const { login, empresaAtivaId, signOut } = useAuth()

  return (
    <Dropdown
      trigger={['click']}
      menu={{
        items: [
          {
            key: 'info',
            disabled: true,
            label: (
              <Flex vertical>
                <Text strong>{login ?? '—'}</Text>
                <Text type="secondary" style={{ fontSize: 12 }}>
                  Empresa ativa: {empresaAtivaId ?? '—'}
                </Text>
              </Flex>
            ),
          },
          { type: 'divider' },
          {
            key: 'sair',
            label: 'Sair',
            icon: <LogoutOutlined />,
            danger: true,
            onClick: () => signOut(),
          },
        ],
      }}
    >
      <Flex align="center" gap={8} style={{ cursor: 'pointer', padding: '0 8px' }}>
        <Avatar icon={<UserOutlined />} />
        <Text>{login ?? '...'}</Text>
      </Flex>
    </Dropdown>
  )
}
