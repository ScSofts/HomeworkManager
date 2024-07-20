import { request } from "../utils"

// 登录请求
export function loginAPI(formData) {
    return request({
        url: '/user/login',
        method: 'POST',
        data: formData
    });
}

// 获取验证码
export function getCaptchaAPI() {
    return request({
        url: '/user/captcha',
        method: 'POST'
    });
}
// 注册请求
export function registerAPI(formData) {
    return request({
        url: 'user/register',
        method: 'POST',
        data: formData
    });
}

// // 获取用户信息
// export function getProfileAPI() {
//     return request({
//         url: '/user/profile',
//         method: 'GET',
//     });
// }

