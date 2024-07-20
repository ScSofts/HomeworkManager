import React from 'react';
import { Layout, Menu, Popconfirm } from 'antd';
import {
    BookOutlined,
    FileTextOutlined,
    UploadOutlined,
    LogoutOutlined,
} from '@ant-design/icons';
import './index.css';
import { Outlet, useLocation, useNavigate } from "react-router-dom";
import {useSelector} from "react-redux";
import {removeToken} from "../../utils";


const { Header, Sider } = Layout;

const items = [
    {
        label: '查看作业',
        key: '/stuHome/viewAssignment',
        icon: <BookOutlined />,
    },
    {
        label: '查看成绩',
        key: '/stuHome/viewScore',
        icon: <FileTextOutlined />,
    },
    {
        label: '提交作业',
        key: '/stuHome/submitAssignment',
        icon: <UploadOutlined />,
    },
    {
        label: '加入班级',
        key: '/stuHome/joinClass',
        icon: <UploadOutlined />,
    }
];

const StuHome = () => {
    const navigate = useNavigate();
    const location = useLocation();

    const onMenuClick = (route) => {
        console.log('菜单被点击了', route);
        const path = route.key;
        navigate(path);
    };

    const selectedKey = location.pathname;

    const onConfirm = () => {
        console.log('确认退出');
        // 这里可以添加退出登录的逻辑，比如清除用户信息
        // dispatch(clearUserInfo());
        removeToken();
        navigate('/login');
    };
    const token=useSelector(state=>state.user.token)
    const username=JSON.parse(window.atob(token.split('.')[1])).username
    // console.log(token)
    return (
        <Layout style={{ height: '100vh' }}>
            <Header className="header">
                <div className="logo">学生作业系统</div>
                <div className="user-info">
                    <span className="user-name">{username}</span>
                    <span className="user-logout">
                        <Popconfirm title="是否确认退出？" okText="退出" cancelText="取消" onConfirm={onConfirm}>
                            <LogoutOutlined /> 退出
                        </Popconfirm>
                    </span>
                </div>
            </Header>
            <Layout>
                <Sider width={200} className="site-layout-background">
                    <Menu
                        mode="inline"
                        theme="dark"
                        selectedKeys={[selectedKey]}
                        onClick={onMenuClick}
                        items={items}
                        style={{ height: '100%', borderRight: 0 }}
                    />
                </Sider>
                <Layout className="layout-content" style={{ padding: 20 }}>
                    <Outlet />
                </Layout>
            </Layout>
        </Layout>
    );
};

export default StuHome;