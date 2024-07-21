import React, {useState, useEffect, useRef} from 'react';
import { Card, Button, Upload, Input, message, Pagination, Select } from 'antd';
import { UploadOutlined, RightOutlined } from '@ant-design/icons';
import { useLocation, useNavigate } from 'react-router-dom';
import { useHomeworkBrief, useHomeworkDetail, useHomework} from "../../hooks/useHomework";
import { useStuClass } from "../../hooks/useStuClass";
import moment from 'moment';
import { useSelector } from 'react-redux';
import { submitHomeworkAPI } from "../../apis/homework";
import {submitImageAPI} from "../../apis/image";
import {baseURL} from "../../utils/request";

const { TextArea } = Input;
const { Option } = Select;

const SubmitAssignment = () => {
    const location = useLocation();
    const navigate = useNavigate();
    const [currentPage, setCurrentPage] = useState(1);
    const [answer, setAnswer] = useState('');
    const [file, setFile] = useState(null);

    const token = useSelector(state => state.user.token);
    const username = JSON.parse(window.atob(token.split('.')[1])).username;
    const role = JSON.parse(window.atob(token.split('.')[1])).role;
    console.log(role)

    // 获取班级列表
    const { classList } = useStuClass();

    // 从路由状态中获取 classId 和 homeworkId
    const initialClassId = location.state?.classId;
    const initialHomeworkId = location.state?.homeworkId;

    const [activeClassId, setActiveClassId] = useState(null);
    const [currentHomeworkId, setCurrentHomeworkId] = useState(null);

    // 获取作业列表
    const { homeworkList } = useHomework(activeClassId);

    useEffect(() => {
        if (classList.length > 0) {
            const firstClassId = initialClassId || classList[0];
            setActiveClassId(firstClassId);
        }
    }, [classList, initialClassId]);

    useEffect(() => {
        if (activeClassId && homeworkList.length > 0) {
            if (initialHomeworkId) {
                const index = homeworkList.findIndex(id => id === initialHomeworkId);
                if (index !== -1) {
                    setCurrentPage(index + 1);
                    setCurrentHomeworkId(initialHomeworkId);
                } else {
                    setCurrentHomeworkId(homeworkList[0]);
                    setCurrentPage(1);
                }
            } else {
                setCurrentHomeworkId(homeworkList[0]);
                setCurrentPage(1);
            }
        }
    }, [activeClassId, homeworkList, initialHomeworkId]);

    // 使用 useHomeworkBrief hook 获取作业标题和时间戳
    const [timestamp, setTimestamp] = useState(null);
    const { title } = useHomeworkBrief(currentHomeworkId, setTimestamp);

    // 使用 useHomeworkDetails hook 获取作业详细内容
    const { content } = useHomeworkDetail(currentHomeworkId);

    const handleClassChange = (value) => {
        setActiveClassId(value);
        setCurrentPage(1);
        setCurrentHomeworkId(null);
        navigate('/stuHome/submitAssignment', { state: { classId: value } });
    };

    const handlePageChange = (page) => {
        setCurrentPage(page);
        setCurrentHomeworkId(homeworkList[page - 1]);
        setAnswer('');
        setFile(null);
    };

    const handleNextAssignment = () => {
        if (currentPage < homeworkList.length) {
            handlePageChange(currentPage + 1);
        }
    };

    const handleAnswerChange = (e) => {
        setAnswer(e.target.value);
        // console.log(answer+"让我康康你")
    };

    const handleFileUpload = (info) => {
        if (info.file) {
            setFile(info.file);
            message.success(`${info.file.name} 文件选择成功`);
        } else if (info.file.status === 'error') {
            message.error(`${info.file.name} 文件上传失败`);
        }
    };

    const handleSubmit = async () => {
        if (!answer && !file) {
            message.warning('请填写答案或上传文件');
            return;
        }
        try {
            const res = await submitImageAPI(file, currentHomeworkId, token, username, role);
            if (res.status === 200) {
                message.success('图片提交成功');
                setAnswer('');
                setFile(null);
            } else {
                message.error('图片提交失败');
            }
        } catch (error) {
            console.error('提交图片时发生错误:', error);
            message.error('图片提交失败');
        }
     // console.log(currentHomeworkId+"你是深恶么"+answer)
        try {
            const res = await submitHomeworkAPI(token, username, currentHomeworkId, answer);
            if (res.status === 200) {
                message.success('作业提交成功');
                setAnswer('');
                setFile(null);
            } else {
                message.error('作业提交失败');
            }
        } catch (error) {
            console.error('提交作业时发生错误:', error);
            message.error('作业提交失败');
        }
    };

    const contentRef = useRef(null);
    useEffect(() => {
        if (contentRef.current && content != null) {
            contentRef.current.innerHTML = content;
        }
    }, [content]);

    const formattedDate = timestamp
        ? moment(timestamp).format('YYYY-MM-DD')
        : '未知日期';

    if (!classList || classList.length === 0) {
        return <div>加载中，或暂无班级...</div>;
    }


    return (
        <div className="submit-assignment">
            <Select
                style={{ width: 200, marginBottom: 20 }}
                value={activeClassId}
                onChange={handleClassChange}
                placeholder="选择班级"
            >
                {classList.map((classId) => (
                    <Option key={classId} value={classId}>班级 {classId}</Option>
                ))}
            </Select>

            {homeworkList && homeworkList.length > 0 ? (
                <>
                    <Card title={title || '作业标题'} style={{ marginBottom: 20 }}>
                        {/*显示作业图片*/}
                        <p><strong>作业图片</strong> </p>
                        <img src={baseURL + "files/homework_"+currentHomeworkId+".png"} alt="作业图片" style={{ width: 180, height: 180 }} />
                        <p><strong>发布时间：</strong>{formattedDate}</p>
                        <p><strong>作业要求：</strong><div ref={contentRef}>{'加载中...'}</div></p>
                        <TextArea
                            rows={4}
                            placeholder="在这里输入你的答案"
                            value={answer}
                            onChange={handleAnswerChange}
                            style={{ marginBottom: 20 }}
                        />
                        <Upload
                            accept="*"
                            onChange={handleFileUpload}
                            fileList={file ? [file] : []}
                            beforeUpload={() => false}
                            multiple={false}
                        >
                            <Button icon={<UploadOutlined />}>上传作业文件</Button>
                        </Upload>
                        <Button
                            type="primary"
                            onClick={handleSubmit}
                            style={{ marginTop: 20, marginRight: 10 }}
                        >
                            提交作业
                        </Button>
                        <Button
                            onClick={handleNextAssignment}
                            disabled={currentPage === homeworkList.length}
                            style={{ marginTop: 20 }}
                        >
                            下一个作业 <RightOutlined />
                        </Button>
                    </Card>
                    <Pagination
                        current={currentPage}
                        total={homeworkList.length}
                        pageSize={1}
                        onChange={handlePageChange}
                        style={{ textAlign: 'center' }}
                    />
                </>
            ) : (
                <div>该班级暂无作业</div>
            )}
        </div>
    );
};

export default SubmitAssignment;