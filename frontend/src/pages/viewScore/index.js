import React, { useState, useEffect, useRef } from 'react';
import { Card, Pagination, Select, message, Spin, Empty } from 'antd';
import './index.css';
import { useStuClass } from "../../hooks/useStuClass";
import {
    useCheckSubmission,
    useGetSubmissionGrade,
    useHomework,
    useHomeworkBrief,
    useHomeworkDetail
} from "../../hooks/useHomework";
import moment from 'moment';

const { Option } = Select;

const ViewScore = () => {
    const [currentPage, setCurrentPage] = useState(1);
    const [activeClassId, setActiveClassId] = useState(null);
    const [isLoading, setIsLoading] = useState(true);

    const [timestamp, setTimestamp] = useState(null);
    const { classList } = useStuClass();
    const { homeworkList, loading: homeworkLoading } = useHomework(activeClassId);

    const selectedHomeworkId = homeworkList[currentPage - 1];

    const { title, loading: briefLoading } = useHomeworkBrief(selectedHomeworkId, setTimestamp);
    const { content: requirements, loading: detailLoading } = useHomeworkDetail(selectedHomeworkId);
    const { content: submittedContent, submissionId, loading: submissionLoading } = useCheckSubmission(selectedHomeworkId);

    const { comment, score, loading: gradeLoading } = useGetSubmissionGrade(submissionId);

    const contentRef = useRef(null);
    const commentRef = useRef(null);

    useEffect(() => {
        if (contentRef.current && requirements != null) {
            contentRef.current.innerHTML = requirements;
        }
    }, [requirements]);

    useEffect(() => {
        if (commentRef.current && comment != null) {
            commentRef.current.innerHTML = comment;
        }
    }, [comment]);

    useEffect(() => {
        if (classList.length > 0 && !activeClassId) {
            setActiveClassId(classList[0]);
        }
    }, [classList, activeClassId]);

    useEffect(() => {
        setIsLoading(homeworkLoading || briefLoading || detailLoading || submissionLoading || gradeLoading);
    }, [homeworkLoading, briefLoading, detailLoading, submissionLoading, gradeLoading]);

    const handleClassChange = (value) => {
        setActiveClassId(value);
        setCurrentPage(1);
    };

    const handlePageChange = (page) => {
        setCurrentPage(page);
    };

    const renderContent = () => {
        if (isLoading) {
            return <Spin tip="加载中..."/>;
        }

        if (homeworkList.length === 0) {
            return <Empty description="当前班级暂无可查看的成绩" />;
        }

        return (
            <>
                <div className="content-section">
                    <div className="left-column">
                        <div className="assignment-section">
                            <h3>提交的作业</h3>
                            {submittedContent ? (
                                <div>
                                    <p>提交的作业内容：</p>
                                    <div>{submittedContent}</div>
                                </div>
                            ) : (
                                <p>暂无提交的作业内容</p>
                            )}
                        </div>
                    </div>
                    <div className="right-column">
                        <div className="homework-info">
                            <Card title={title || "作业标题"} className="assignment-info">
                                <p><strong>发布时间：</strong>{timestamp ? moment(timestamp).format('YYYY-MM-DD HH:mm:ss') : '未知'}</p>
                                <p><strong>作业要求：</strong>
                                    <div ref={contentRef}>{'加载中...'}</div>
                                </p>
                            </Card>
                        </div>
                        <div className="grading-section">
                            <div className="score-details">
                                <h3>作业评分</h3>
                                <h4>总分：{score || "暂无评分"}</h4>
                                <label>教师评语：</label>
                                <p> <div ref={commentRef}>{'加载中...'}</div>
                                </p>
                            </div>
                        </div>
                    </div>
                </div>
                <Pagination
                    current={currentPage}
                    total={homeworkList.length}
                    pageSize={1}
                    onChange={handlePageChange}
                    style={{ textAlign: 'center', marginTop: 20 }}
                />
            </>
        );
    };

    return (
        <div className="view-score-container">
            <div className="selection-section">
                <Select
                    style={{ width: 200, marginRight: 20 }}
                    placeholder="选择班级"
                    onChange={handleClassChange}
                    value={activeClassId}
                >
                    {classList.map((classId) => (
                        <Option key={classId} value={classId}>班级 {classId}</Option>
                    ))}
                </Select>
            </div>
            {renderContent()}
        </div>
    );
};

export default ViewScore;