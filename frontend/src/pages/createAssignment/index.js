import React, { useState } from 'react';
import { Select, Upload, Button, message, Modal, Input, DatePicker, Spin } from 'antd';
import { UploadOutlined, PlusOutlined } from '@ant-design/icons';
import ReactQuill from 'react-quill';
import 'react-quill/dist/quill.snow.css';
import './index.css';
import { useClass } from "../../hooks/useClass";
import { createHomeworkAPI, updateHomeworkAPI } from "../../apis/homework";
import { useSelector } from "react-redux";
import { submitImageAPI } from "../../apis/image";

const { Option } = Select;

const PublishAssignment = () => {
    const { classList } = useClass();
    const [selectedClass, setSelectedClass] = useState('');
    const [imageFile, setImageFile] = useState(null);
    const [requirements, setRequirements] = useState('');
    const [isModalVisible, setIsModalVisible] = useState(false);
    const [newAssignmentTitle, setNewAssignmentTitle] = useState('');
    const [deadline, setDeadline] = useState(null);
    const [homeworkId, setHomeworkId] = useState(null);
    const [title, setTitle] = useState('');
    const [isLoading, setIsLoading] = useState(false);

    const token = useSelector(state => state.user.token);
    const { username, role } = JSON.parse(window.atob(token.split('.')[1]));

    const handleClassChange = (value) => {
        setSelectedClass(value);
    };

    const handleImageUpload = (info) => {
        if (info.file) {
            setImageFile(info.file);
            message.success(`${info.file.name} 图片选择成功`);
        } else if (info.file.status === 'error') {
            message.error(`${info.file.name} 图片上传失败`);
        }
    };

    const handleRequirementsChange = (content) => {
        setRequirements(content);
    };

    const showModal = () => {
        setIsModalVisible(true);
    };

    const handleOk = async () => {
        if (newAssignmentTitle.trim() === '') {
            message.warning('请输入作业标题');
            return;
        }

        const newTitle = newAssignmentTitle;
        setTitle(newTitle);
        setIsModalVisible(false);
        setNewAssignmentTitle('');
        setIsLoading(true);

        try {
            const res = await createHomeworkAPI(token, username, selectedClass, newTitle);
            if (res.status === 200) {
                setHomeworkId(res.data);
                message.success('新作业创建成功');
            } else {
                message.error('新作业创建失败');
            }
        } catch (error) {
            console.error('创建作业时发生错误:', error);
            const res = error.response?.data;
            if (!res?.error) {
                message.error('创建失败，未知错误');
            } else if (res.error === "Validation Failed") {
                res.data.forEach(i => message.error(i));
            } else {
                message.error(res.error);
            }
        } finally {
            setIsLoading(false);
        }
    };

    const handleDateChange = (date) => {
        setDeadline(date);
    };

    const handleCancel = () => {
        setIsModalVisible(false);
        setNewAssignmentTitle('');
    };

    const handlePublish = async () => {
        if (!selectedClass || !imageFile || !requirements || !deadline || !homeworkId) {
            message.warning('请填写所有必要信息，包括截止日期和创建新作业');
            return;
        }

        setIsLoading(true);

        try {
            const imageRes = await submitImageAPI(imageFile, homeworkId, token, username, role);
            if (imageRes.status === 200) {
                message.success('图片提交成功');
            } else {
                throw new Error('图片提交失败');
            }

            const homeworkRes = await updateHomeworkAPI(token, username, homeworkId, title, requirements, deadline.format('YYYY-MM-DD'));
            if (homeworkRes.status === 200) {
                message.success('作业发布成功');
                // 重置表单
                setSelectedClass('');
                setImageFile(null);
                setRequirements('');
                setDeadline(null);
                setHomeworkId(null);
                setTitle('');
            } else {
                throw new Error('作业发布失败');
            }
        } catch (error) {
            console.error('发布作业失败:', error);
            message.error(error.message || '作业发布失败');
        } finally {
            setIsLoading(false);
        }
    };

    const modules = {
        toolbar: [
            [{ 'header': [1, 2, false] }],
            ['bold', 'italic', 'underline', 'strike', 'blockquote'],
            [{'list': 'ordered'}, {'list': 'bullet'}, {'indent': '-1'}, {'indent': '+1'}],
            ['link', 'image'],
            ['clean']
        ],
    };

    const formats = [
        'header',
        'bold', 'italic', 'underline', 'strike', 'blockquote',
        'list', 'bullet', 'indent',
        'link', 'image'
    ];

    return (
        <Spin spinning={isLoading}>
            <div className="publish-assignment">
                <h2>发布作业</h2>
                <div className="form-item">
                    <label>选择班级：</label>
                    <Select
                        placeholder="选择班级"
                        onChange={handleClassChange}
                        value={selectedClass}
                        style={{ width: '60%' }}
                    >
                        {classList.map((classItem) => (
                            <Option key={classItem} value={classItem}>
                                班级 {classItem}
                            </Option>
                        ))}
                    </Select>
                </div>
                <div className="form-item" style={{ marginTop: '10px' }}>
                    <Button type="primary" icon={<PlusOutlined />} onClick={showModal}>
                        新建作业
                    </Button>
                </div>
                {title && <div className="form-item">当前作业标题: {title}</div>}
                <div className="form-item">
                    <label>上传作业图片：</label>
                    <Upload
                        accept="image/*"
                        onChange={handleImageUpload}
                        beforeUpload={() => false}
                    >
                        <Button icon={<UploadOutlined />}>上传作业图片</Button>
                    </Upload>
                </div>
                <div className="form-item">
                    <label>作业要求：</label>
                    <ReactQuill
                        theme="snow"
                        value={requirements}
                        onChange={handleRequirementsChange}
                        modules={modules}
                        formats={formats}
                    />
                </div>
                <div className="form-item">
                    <label>截止日期：</label>
                    <DatePicker
                        format="YYYY-MM-DD"
                        placeholder="选择截止日期"
                        onChange={handleDateChange}
                        value={deadline}
                    />
                </div>
                <Button type="primary" onClick={handlePublish} disabled={!homeworkId}>发布作业</Button>

                <Modal
                    title="新建作业"
                    visible={isModalVisible}
                    onOk={handleOk}
                    onCancel={handleCancel}
                >
                    <Input
                        placeholder="请输入作业标题"
                        value={newAssignmentTitle}
                        onChange={(e) => setNewAssignmentTitle(e.target.value)}
                    />
                </Modal>
            </div>
        </Spin>
    );
};

export default PublishAssignment;