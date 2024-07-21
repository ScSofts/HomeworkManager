import React, { useState, useEffect } from 'react';
import { List, Card, Button, Pagination, Select } from 'antd';
import { useNavigate } from 'react-router-dom';
import { useStuClass } from "../../hooks/useStuClass";
import { useHomework, useHomeworkBrief } from "../../hooks/useHomework";
import moment from 'moment';
import {baseURL} from "../../utils/request";

const { Option } = Select;

const HomeworkItem = ({ homeworkId, classId, onViewDetails }) => {
    const [timestamp, setTimestamp] = useState(null);
    const { title } = useHomeworkBrief(homeworkId, setTimestamp);
    const formattedDate = timestamp
        ? moment(timestamp).format('YYYY-MM-DD')
        : '未知日期';

    return (
        <List.Item>
            <Card
                hoverable
                cover={
                    <div style={{ height: 200, display: 'flex', justifyContent: 'center', alignItems: 'center', background: '#f0f0f0' }}>
                        <img
                            src={baseURL + `files/homework_${homeworkId}.png`}
                            alt="作业图片"
                            style={{ maxWidth: '100%', maxHeight: '100%', objectFit: 'contain' }}
                        />
                    </div>
                }
            >
                <Card.Meta
                    title={`作业标题: ${title || '加载中...'}`}
                    description={`发布时间: ${formattedDate}`}
                />
                <div style={{ marginTop: 16 }}>
                    <Button type="primary" onClick={() => onViewDetails(classId, homeworkId)}>
                        查看详情
                    </Button>
                </div>
            </Card>
        </List.Item>
    );
};

const ViewAssignment = () => {
    const [currentPage, setCurrentPage] = useState(1);
    const [activeClassId, setActiveClassId] = useState(null);
    const pageSize = 3;
    const navigate = useNavigate();

    const { classList } = useStuClass();
    const { homeworkList } = useHomework(activeClassId);

    useEffect(() => {
        if (classList.length > 0) {
            setActiveClassId(classList[0]);
        }
    }, [classList]);

    const handleViewDetails = (classId, homeworkId) => {
        navigate('/stuHome/submitAssignment', { state: { classId, homeworkId } });
    };

    const handlePageChange = (page) => {
        setCurrentPage(page);
    };

    const handleClassChange = (value) => {
        setActiveClassId(value);
        setCurrentPage(1);
    };

    const renderAssignments = () => {
        if (!homeworkList || homeworkList.length === 0) {
            return <div>此班级暂无作业。</div>;
        }

        const startIndex = (currentPage - 1) * pageSize;
        const endIndex = startIndex + pageSize;
        const currentAssignments = homeworkList.slice(startIndex, endIndex);

        return (
            <>
                <List
                    grid={{ gutter: 16, column: 3 }}
                    dataSource={currentAssignments}
                    renderItem={homeworkId => (
                        <HomeworkItem key={homeworkId} homeworkId={homeworkId} classId={activeClassId} onViewDetails={handleViewDetails} />
                    )}
                />
                <Pagination
                    current={currentPage}
                    total={homeworkList.length}
                    pageSize={pageSize}
                    onChange={handlePageChange}
                    style={{ marginTop: '20px', textAlign: 'center' }}
                />
            </>
        );
    };

    return (
        <div className="view-assignment">
            <h2>作业列表</h2>
            <Select
                style={{ width: 200, marginBottom: 20 }}
                placeholder="选择班级"
                onChange={handleClassChange}
                value={activeClassId}
            >
                {classList.map((classId) => (
                    <Option key={classId} value={classId}>
                        班级 {classId}
                    </Option>
                ))}
            </Select>
            {renderAssignments()}
        </div>
    );
};

export default ViewAssignment;