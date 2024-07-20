import axios from "axios";
import { getToken, removeToken } from "./token";
import { useNavigate } from 'react-router-dom';

const development = 'http://localhost:8000/';
const production = '/';
const request = axios.create({
    baseURL:  process.env.NODE_ENV === 'development' ? development : production, // 根据你的实际后端配置进行修改
    timeout: 5000
});

// 添加请求拦截器
request.interceptors.request.use(
    (config) => {
        const token = getToken();
        if (token) {
            config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
    },
    (error) => {
        return Promise.reject(error);
    }
);

// 添加响应拦截器
request.interceptors.response.use(
    (response) => {
        return response.data;
    },
    (error) => {
        // console.error('Response Error:', error);
        if (error.response && error.response.status === 401) {
            removeToken();
            // useNavigate('/login'); // 使用 router 导航到登录页面
        }
        return Promise.reject(error);
    }
);

export { request };
