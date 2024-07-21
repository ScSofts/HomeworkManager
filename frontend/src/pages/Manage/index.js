import React, { useState, useEffect, useCallback } from 'react';
import { Table, Button, Select, Spin } from 'antd';
import { useNavigate, useLocation } from 'react-router-dom';
import { useClass } from '../../hooks/useClass';
import { useSubmission, useHomeworkTitle, useSubmittedHomework, useSubmissionBrief } from '../../hooks/useHomework';
import { useSelector } from 'react-redux';
import './index.css';

const { Option } = Select;

const Manage = () => {
    const navigate = useNavigate();
    const location = useLocation();
    const [selectedClassId, setSelectedClassId] = useState(null);
    const [selectedHomeworkId, setSelectedHomeworkId] = useState(null);
    const [tableData, setTableData] = useState([]);
    const [homeworkTitles, setHomeworkTitles] = useState({});
    const [loading, setLoading] = useState(true);

    const token = useSelector(state => state.user.token);
    const username = JSON.parse(window.atob(token.split('.')[1])).username;

    const { classList } = useClass(token, username);
    const { homeworkList } = useSubmission(selectedClassId);

    useHomeworkTitle(homeworkList, setHomeworkTitles);
    const { submissionList } = useSubmittedHomework(selectedHomeworkId, loading);

    const {submissionBriefs, selectedHomeworkId: currentSelectedHomeworkId} = useSubmissionBrief(selectedHomeworkId, submissionList, loading, setLoading);

    // 初始化选择第一个班级
    useEffect(() => {
        const state = location.state;
        if (state?.fromGrade) {
            setSelectedClassId(state.selectedClassId);
            setSelectedHomeworkId(state.selectedHomeworkId);
        } else if (classList.length > 0 && !selectedClassId) {
            setSelectedClassId(classList[0]);
        }
    }, [location, classList]);

    // 初始化选择第一个作业
    useEffect(() => {
        if (homeworkList.length > 0 && !selectedHomeworkId) {
            setSelectedHomeworkId(homeworkList[0]);
        }
    }, [homeworkList]);

    useEffect(() => {
        if (submissionList && submissionList.length > 0) {
            setTableData(submissionList.map((submissionId) => ({
                index: submissionId,
                key: submissionId,
                studentName: '加载中...',
                assignmentStatus: '加载中...',
                submissionId,
                selectedClassId
            })));
            setLoading(true);
        } else {
            setTableData([]);
            setLoading(false);
        }
    }, [submissionList]);

    const updateStudentNameAndStatus = useCallback(() => {
        if (submissionBriefs.length === submissionList.length && currentSelectedHomeworkId === selectedHomeworkId) {
            setTableData(prev =>
                submissionBriefs.map(({submission_id, username, status}) => {
                    for (const item of prev) {
                        if (item.submissionId === submission_id) {
                            return {
                                ...item,
                                studentName: username,
                                assignmentStatus: status === 'PENDING' ? '未批改' :
                                    status === 'ACCEPTED' ? '已批改' : '未提交'
                            }
                        }
                    }
                    return prev;
                })
            );
            setLoading(false);
        }
    }, [submissionBriefs, submissionList.length]);

    useEffect(() => {
        updateStudentNameAndStatus();
    }, [updateStudentNameAndStatus]);

    const columns = [
        {
            title: '学生姓名',
            dataIndex: 'studentName',
            key: 'studentName'
        },
        {
            title: '作业状态',
            dataIndex: 'assignmentStatus',
            key: 'assignmentStatus',
            render: (status) => (
                <span className={`status-tag ${
                    status === '未批改' ? 'status-pending' :
                        status === '已批改' ? 'status-graded' :
                            'status-unsubmitted'
                }`}>
                    {status}
                </span>
            )
        },
        {
            title: '操作',
            dataIndex: 'operation',
            key: 'operation',
            render: (_, record) => (
                <Button
                    type="link"
                    onClick={() => handleGradeAssignment(record)}
                    className={record.assignmentStatus === '未批改' ? 'button-grade' : 'button-disabled'}
                    disabled={record.assignmentStatus !== '未批改'}
                >
                    批改作业
                </Button>
            )
        }
    ];

    const handleGradeAssignment = (record) => {
        if (record.assignmentStatus === '未批改') {
            navigate(`/gradeAssignment/grade`, {
                state: {
                    submissionId: record.submissionId,
                    selectedClassId,
                    selectedHomeworkId
                }
            });
        }
    };

    const handleClassChange = (value) => {
        setSelectedClassId(value);
        setSelectedHomeworkId(null);
        setTableData([]);
        setHomeworkTitles({});
        setLoading(true);
    };

    const handleHomeworkChange = (value) => {
        setSelectedHomeworkId(value);
        setTableData([]);
        setLoading(true);
    };

    return (
        <div className="manage-container">
            <h2 className="manage-title">作业管理模块</h2>
            <div style={{ marginBottom: 20 }}>
                <Select
                    style={{ width: 200, marginRight: 20 }}
                    placeholder="选择班级"
                    onChange={handleClassChange}
                    value={selectedClassId}
                >
                    {classList.map((classId) => (
                        <Option key={classId} value={classId}>班级 {classId}</Option>
                    ))}
                </Select>
                <Select
                    style={{ width: 200 }}
                    placeholder="选择作业"
                    onChange={handleHomeworkChange}
                    value={selectedHomeworkId}
                    disabled={!selectedClassId}
                >
                    {homeworkList.map((homeworkId) => (
                        <Option key={homeworkId} value={homeworkId}>
                            {homeworkTitles[homeworkId] || `作业 ${homeworkId}`}
                        </Option>
                    ))}
                </Select>
            </div>
            <div className="table-container">
                <Spin spinning={loading}>
                    <Table
                        columns={columns}
                        dataSource={tableData}
                        pagination={{ pageSize: 6 }}
                        scroll={{ y: 720 }}
                        className="custom-table"
                    />
                </Spin>
            </div>
        </div>
    );
};

export default Manage;