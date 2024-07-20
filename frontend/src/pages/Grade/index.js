import React, { useEffect, useState } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import { InputNumber, message, Spin, Alert } from 'antd';
import ReactQuill from 'react-quill';
import 'react-quill/dist/quill.snow.css';
import './index.css';

import { useSubmissionDetail } from "../../hooks/useHomework";
import { gradeSubmissionAPI } from "../../apis/homework";

import { useSelector } from "react-redux";


const Grade = () => {
    const location = useLocation();
    const navigate = useNavigate();
    const { submissionId,selectedHomeworkId } = location.state || {};
    // console.log(submissionId+"asdfaff")
    const [assignmentImage, setAssignmentImage] = useState('');
    const [manualTotalScore, setManualTotalScore] = useState(0);
    const [comments, setComments] = useState('');
    const [isLoading, setIsLoading] = useState(true);

    const { name: username, content, status, score, teacherComment } = useSubmissionDetail(submissionId);

    const token = useSelector(state => state.user.token);
    const username1 = JSON.parse(window.atob(token.split('.')[1])).username;

    useEffect(() => {
        if (submissionId) {
            setAssignmentImage(`/files/submit_${username}_${selectedHomeworkId}.png`);
        }
        setIsLoading(false);
    }, [submissionId, username]);

    const handleManualTotalScoreChange = (value) => {
        setManualTotalScore(value);
    };

    const handleCommentsChange = (content) => {
        setComments(content);
    };

    const handleSubmit = async () => {
        try {
            const res = await gradeSubmissionAPI(token, username1, submissionId, manualTotalScore, comments);
            if (res.status === 200) {
                message.success('提交成功');
                navigate('/gradeAssignment');
            } else {
                message.error(res.error || '提交失败，请确认你的批改过程全部完成');
            }
        } catch (error) {
            console.error('提交批改结果失败', error);
            const res = error.response?.data;
            if (!res?.error) {
                message.error('提交失败，未知错误');
            } else if (res.error === "Validation Failed") {
                res.data.forEach(i => message.error(i));
            } else {
                message.error(res.error);
            }
        }
    };

    const modules = {
        toolbar: [
            [{ 'header': [1, 2, false] }],
            ['bold', 'italic', 'underline', 'strike', 'blockquote'],
            [{'list': 'ordered'}, {'list': 'bullet'}, {'indent': '-1'}, {'indent': '+1'}],
            ['link'],
            ['clean']
        ],
    };

    const formats = [
        'header',
        'bold', 'italic', 'underline', 'strike', 'blockquote',
        'list', 'bullet', 'indent',
        'link'
    ];

    if (isLoading) {
        return <Spin tip="加载中..." />;
    }

    if (status === 'graded') {
        return (
            <Alert
                message="作业已批改"
                description={
                    <div>
                        <p>这份作业已经被批改过了。</p>
                        <p>分数: {score}</p>
                        <p>教师评语: {teacherComment}</p>
                        <button onClick={() => navigate('gradeAssignment/manage')}>返回管理页面</button>
                    </div>
                }
                type="info"
                showIcon
            />
        );
    }

    return (
        <div className="grade-container">
            <div className="assignment-section">
                <h3>学生 {username}</h3>
                {assignmentImage && (
                    <img src={assignmentImage} alt="学生作业" className="assignment-image" />
                )}
                <div className="student-content">
                    <h4>学生回答：</h4>
                    <p>{content}</p>
                </div>
            </div>
            <div className="grading-section">
                <h3>批改区域</h3>
                <div className="question-scores">
                    <h4>总分：</h4>
                    <InputNumber
                        min={0}
                        max={100}
                        value={manualTotalScore}
                        onChange={handleManualTotalScoreChange}
                        placeholder="输入分数"
                    />
                    <br />
                    <label>评语：</label>
                    <ReactQuill
                        theme="snow"
                        value={comments}
                        onChange={handleCommentsChange}
                        modules={modules}
                        formats={formats}
                    />
                    <br />
                    <button className="submit-button" onClick={handleSubmit}>提交批改结果</button>
                </div>
            </div>
        </div>
    );
};

export default Grade;