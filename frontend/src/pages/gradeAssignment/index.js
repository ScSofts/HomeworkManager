import React, { useEffect } from 'react'
import { Outlet } from 'react-router-dom'
import { message } from 'antd'
import { mockFetchUserInfo } from '../../store/modules/mockUserData'

const GradeAssignment = () => {
    useEffect(() => {
        const loadUserInfo = async () => {
            try {
                const res = await mockFetchUserInfo()
                console.log('User Info:', res)
            } catch (error) {
                message.error('获取用户信息失败')
                console.error('Get User Info error:', error)
            }
        }
        loadUserInfo()
    }, [])

    return (
        <div>
            <h2>批改作业</h2>
            <Outlet />
        </div>
    )
}

export default GradeAssignment