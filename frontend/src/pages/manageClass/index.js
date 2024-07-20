import React, { useState, useEffect } from 'react';
import { Button, message, Select, Table, Pagination, Spin, Empty } from 'antd';
import { useSelector } from 'react-redux';
import { useClass } from '../../hooks/useClass';
import { useStudent } from '../../hooks/useStudent';
import { createClassAPI } from '../../apis/class';

const { Option } = Select;

const ManageClass = () => {
    const [isLoading, setIsLoading] = useState(false);
    const [selectedClass, setSelectedClass] = useState(null);
    const [currentPage, setCurrentPage] = useState(1);
    const pageSize = 10;

    const token = useSelector(state => state.user.token);
    const username = JSON.parse(window.atob(token.split('.')[1])).username;

    const { classList, refetchClassList } = useClass();
    const { studentList } = useStudent(selectedClass);

    const [displayStudentList, setDisplayStudentList] = useState([]);

    useEffect(() => {
        if (classList.length > 0 && !selectedClass) {
            setSelectedClass(classList[0]);
        }
    }, [classList, selectedClass]);

    useEffect(() => {
        if (studentList && Array.isArray(studentList)) {
            setDisplayStudentList(studentList);
        } else {
            setDisplayStudentList([]);
        }
    }, [studentList]);

    const handleCreateClass = async () => {
        try {
            setIsLoading(true);
            const response = await createClassAPI(token, username);

            if (response.status === 200) {
                message.success('班级创建成功');
                await refetchClassList();
                // 如果需要，可以在这里更新选中的班级
                // setSelectedClass(response.data.newClassId); // 假设 API 返回新创建的班级 ID
            } else {
                message.error('班级创建失败');
            }
        } catch (error) {
            console.error('创建班级时发生错误:', error);
            message.error('创建班级失败: ' + (error.response?.data?.message || '未知错误'));
        } finally {
            setIsLoading(false);
        }
    };

    const handleClassChange = (value) => {
        setSelectedClass(value);
        setCurrentPage(1);
    };

    const columns = [
        {
            title: '学生姓名',
            dataIndex: 'username',
            key: 'username',
        },
        {
            title: '学号',
            dataIndex: 'student_id',
            key: 'student_id',
        },
        {
            title: '创建时间',
            dataIndex: 'created_at',
            key: 'created_at',
            render: (text) => new Date(text).toLocaleString(),
        },
        {
            title: '最后登录',
            dataIndex: 'last_login',
            key: 'last_login',
            render: (text) => new Date(text).toLocaleString(),
        },
    ];

    const paginatedData = displayStudentList.slice((currentPage - 1) * pageSize, currentPage * pageSize);

    return (
        <div style={{ padding: '20px' }}>
            <h1>管理班级</h1>
            <Spin spinning={isLoading}>
                <div style={{ marginBottom: '30px' }}>
                    <h2>创建新班级</h2>
                    <Button type="primary" onClick={handleCreateClass}>
                        创建班级
                    </Button>
                </div>

                <div>
                    <h2>班级学生列表</h2>
                    {classList.length > 0 ? (
                        <>
                            <Select
                                style={{ width: 200, marginBottom: 20 }}
                                placeholder="选择班级"
                                onChange={handleClassChange}
                                value={selectedClass}
                            >
                                {classList.map((classItem) => (
                                    <Option key={classItem} value={classItem}>
                                        班级 {classItem}
                                    </Option>
                                ))}
                            </Select>

                            {displayStudentList.length > 0 ? (
                                <>
                                    <Table
                                        columns={columns}
                                        dataSource={paginatedData}
                                        pagination={false}
                                        rowKey="student_id"
                                    />
                                    <Pagination
                                        current={currentPage}
                                        total={displayStudentList.length}
                                        pageSize={pageSize}
                                        onChange={(page) => setCurrentPage(page)}
                                        style={{ marginTop: 20, textAlign: 'right' }}
                                    />
                                </>
                            ) : (
                                <Empty description="该班级暂无学生，请邀请学生加入" />
                            )}
                        </>
                    ) : (
                        <Empty description="暂无班级，请先创建班级" />
                    )}
                </div>
            </Spin>
        </div>
    );
};

export default ManageClass;