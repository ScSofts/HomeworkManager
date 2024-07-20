import React, { useState } from 'react';
import { Input, Button, List, message, Spin, Pagination } from 'antd';
import { useGetStuJoinClass } from '../../hooks/useClass';
import { joinStudentAPI } from '../../apis/student';
import { useSelector } from 'react-redux';

const JoinClass = () => {
    const [classroomId, setClassroomId] = useState('');
    const [isJoining, setIsJoining] = useState(false);
    const [currentPage, setCurrentPage] = useState(1);
    const pageSize = 6; // 每页显示 6 个班级
    const { classList, refetchClassList } = useGetStuJoinClass();
    const token = useSelector(state => state.user.token);
    const username = JSON.parse(window.atob(token.split('.')[1])).username;

    const handleJoinClass = async () => {
        if (!classroomId.trim()) {
            message.error('请输入班级ID');
            return;
        }

        setIsJoining(true);
        try {
            const response = await joinStudentAPI(token, username, classroomId);
            if (response.status === 200) {
                message.success('成功加入班级');
                setClassroomId('');
                await refetchClassList();
                // 如果新加入的班级不在当前页，跳转到最后一页
                const totalPages = Math.ceil((classList.length + 1) / pageSize);
                setCurrentPage(totalPages);
            } else {
                message.error('加入班级失败');
            }
        } catch (error) {
            console.error('加入班级时发生错误:', error);
            message.error('班级ID输入错误或加入失败');
        } finally {
            setIsJoining(false);
        }
    };

    // 计算当前页应该显示的班级
    const currentClasses = classList.slice(
        (currentPage - 1) * pageSize,
        currentPage * pageSize
    );

    return (
        <div style={{ padding: '20px' }}>
            <h1>加入班级</h1>
            <div style={{ marginBottom: '20px' }}>
                <Input
                    placeholder="输入班级ID"
                    value={classroomId}
                    onChange={(e) => setClassroomId(e.target.value)}
                    style={{ width: '200px', marginRight: '10px' }}
                />
                <Button type="primary" onClick={handleJoinClass} loading={isJoining}>
                    加入班级
                </Button>
            </div>

            <h2>已加入的班级</h2>
            <Spin spinning={isJoining}>
                {classList.length > 0 ? (
                    <>
                        <List
                            bordered
                            dataSource={currentClasses}
                            renderItem={item => (
                                <List.Item>
                                    班级 {item}
                                </List.Item>
                            )}
                        />
                        <Pagination
                            current={currentPage}
                            total={classList.length}
                            pageSize={pageSize}
                            onChange={(page) => setCurrentPage(page)}
                            style={{ marginTop: '20px', textAlign: 'right' }}
                        />
                    </>
                ) : (
                    <p>暂未加入任何班级</p>
                )}
            </Spin>
        </div>
    );
};

export default JoinClass;