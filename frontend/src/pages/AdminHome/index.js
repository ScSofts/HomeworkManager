import { Layout, Menu, Popconfirm, message } from 'antd'
import {
    DiffOutlined,
    EditOutlined,
    LogoutOutlined,
} from '@ant-design/icons'
import './index.css'
import { Outlet, useLocation, useNavigate } from "react-router-dom";
import { useEffect, useState } from 'react'
import { mockFetchUserInfo, mockClearUserInfo } from '../../store/modules/mockUserData'
import {useSelector} from "react-redux";
import {getClassAPI} from "../../apis/class";
import {removeToken} from "../../utils";

const { Header, Sider, Content } = Layout

const items = [
    {
        label: '批改作业',
        key: '/gradeAssignment',
        icon: <EditOutlined />,
    },
    {
        label: '发布作业',
        key: '/createAssignment',
        icon: <EditOutlined />,
    },
    {
        label: '查看统计结果',
        key: '/assignmentStats',
        icon: <DiffOutlined />,
    },
    {
        label: '管理班级',
        key: '/manageClass',
        icon: <DiffOutlined />,
    },
]

const GeekLayout = () => {
    const navigate = useNavigate()
    const location = useLocation()
    const [userInfo, setUserInfo] = useState(null)

    const onMenuClick = (route) => {
        console.log('菜单被点击了', route)
        const path = route.key
        navigate(path)
    }

    const selectedkey = location.pathname

    useEffect(() => {
        const fetchUserInfo = async () => {
            try {
                const userData = await mockFetchUserInfo()
                setUserInfo(userData)
            } catch (error) {
                console.error('获取用户信息失败', error)
                message.error('获取用户信息失败')
            }
        }
        fetchUserInfo()
    }, [])

    const onConfirm = async () => {
        try {
            setUserInfo(null)
            removeToken();
            message.success('退出成功')
            navigate('/login')
        } catch (error) {
            console.error('退出失败', error)
            message.error('退出失败')
        }
    }
    const token=useSelector(state=>state.user.token)
    const username=JSON.parse(window.atob(token.split('.')[1])).username
    const res= getClassAPI(token,username)
    console.log(res)
    console.log(username)
    return (
        <Layout className="app-layout">
            <Header className="app-header">
                <div className="logo">教师管理系统</div>
                <div className="user-info">
                    <span className="user-name">{username}</span>
                    <Popconfirm title="是否确认退出？" okText="退出" cancelText="取消" onConfirm={onConfirm}>
                        <LogoutOutlined className="logout-icon" /> 退出
                    </Popconfirm>
                </div>
            </Header>
            <Layout>
                <Sider width={200} className="app-sider">
                    <Menu
                        mode="inline"
                        theme="dark"
                        selectedKeys={[selectedkey]}
                        onClick={onMenuClick}
                        items={items}
                    />
                </Sider>
                <Content className="app-content">
                    <Outlet />
                </Content>
            </Layout>
        </Layout>
    )
}

export default GeekLayout