import React, { useState, useEffect, useRef } from 'react';
import './index.css';
import { Card, Form, Input, Button, Radio, message } from 'antd';
import logo from '../../assets/logo.jpg';
import { useDispatch } from 'react-redux';
import {fetchLogin, setUserInfo} from '../../store/modules/user';
import { useNavigate } from 'react-router-dom';
import { getCaptchaAPI, loginAPI } from '../../apis/user';

const Login = () => {
    const dispatch = useDispatch();
    const navigate = useNavigate();
    const [captchaImg, setCaptchaImg] = useState('');
    const [token, setToken] = useState(null);
    const [form] = Form.useForm();
    const [timeout, setTimeoutFlag] = useState(false);
    const timeout_id = useRef(null);

    const fetchCaptcha = async () => {
        clearTimeout(timeout_id.current);
        try {
            const response = await getCaptchaAPI();
            const imageUrl = `data:image/png;base64,${response.data.image}`;
            const token = response.data.token;
            setCaptchaImg(imageUrl);
            setToken(token);
            setTimeoutFlag(false);
            timeout_id.current = setTimeout(() => setTimeoutFlag(true), 60 * 1000);
        } catch (error) {
            message.error('获取验证码失败');
        }
    };

    useEffect(() => {
        if (token == null || timeout) {
            fetchCaptcha();
        }
    }, [token, timeout]);

    const onFinish = async (values) => {
        try {
            const loginData = {
                ...values,
                token
            };
            const res = await loginAPI(loginData);
            if (res.status === 200) {
                // 保存 token
                await dispatch(fetchLogin(res.data.token));
                // {JSON解析token}
                 const token = JSON.parse(window.atob(res.data.token.split('.')[1]));
                 console.log(token);

                if (values.role === 'STUDENT') {
                    navigate('/stuHome/viewAssignment');
                } else if (values.role === 'TEACHER') {
                    navigate('/gradeAssignment');
                }
                message.success('登录成功');
            } else {
                message.error(res.error || '登录失败，请检查您的输入');
                fetchCaptcha();
                form.setFieldsValue({ captcha: '' });
            }
        } catch (error) {
            const res = error.response.data;
            if(!res.error){
                message.error('登录失败，未知错误');
            }
            if(res.error === "Validation Failed"){
                for(const i of res.data){
                    message.error(i);
                }
            }else{
                message.error(res.error);
            }
            fetchCaptcha();
            form.setFieldsValue({ captcha: '' });
        }
    };

    const onRegister = () => {
        navigate('/register');
    };

    return (
        <div className="login">
            <Card className="login-container">
                <img className="login-logo" src={logo} alt="logo" />
                <Form form={form} onFinish={onFinish} validateTrigger={["onBlur", "onChange"]}>
                    <Form.Item
                        name="username"
                        rules={[
                            { required: true, message: '请输入用户名' },
                            { pattern: /^[A-Za-z]{4,8}$/, message: '请输入正确的用户名' }
                        ]}
                    >
                        <Input size="large" placeholder="请输入用户名" />
                    </Form.Item>
                    <Form.Item
                        name="password"
                        rules={[
                            { required: true, message: '请输入密码' },
                            { pattern: /^[A-Za-z1-9]{4,12}$/, message: '请输入正确的密码' }
                        ]}
                    >
                        <Input size="large" type="password" placeholder="请输入密码" />
                    </Form.Item>
                    <Form.Item
                        name="role"
                        rules={[{ required: true, message: '请选择一个身份' }]}
                    >
                        <Radio.Group>
                            <Radio value="TEACHER">我是教师</Radio>
                            <Radio value="STUDENT">我是学生</Radio>
                        </Radio.Group>
                    </Form.Item>
                    <Form.Item className="captcha-box">
                        <Form.Item
                            name="captcha"
                            noStyle
                            rules={[{ required: true, message: '请输入验证码' }]}
                        >
                            <Input size="large" placeholder="请输入验证码" style={{ width: '50%' }} />
                        </Form.Item>
                        <img
                            src={captchaImg}
                            alt="验证码图片"
                            onClick={fetchCaptcha}
                            style={{ width: '40%', height: '32px', marginLeft: '10%', cursor: 'pointer' }}
                        />
                    </Form.Item>
                    <Form.Item>
                        <Button type="primary" htmlType="submit" size="large" block>
                            登录
                        </Button>
                    </Form.Item>
                </Form>
                <Button type="default" size="large" block onClick={onRegister}>
                    注册
                </Button>
            </Card>
        </div>
    );
};

export default Login;