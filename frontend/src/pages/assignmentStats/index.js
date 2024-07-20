import React, { useState, useEffect, useRef } from 'react';
import { Spin, Table, Card, Button, Select, Row, Col, Empty } from 'antd';
import * as echarts from 'echarts';
import { useClass, useGetClassStatistics } from "../../hooks/useClass";

const { Option } = Select;

const AssignmentStats = () => {
    const [view, setView] = useState('chart');
    const [selectedClassId, setSelectedClassId] = useState('');
    const [selectedHomeworkId, setSelectedHomeworkId] = useState('');
    const chartRef = useRef(null);

    const { classList } = useClass();
    const { homeworkStats, studentGrades, loading, error } = useGetClassStatistics(selectedClassId);

    useEffect(() => {
        if (classList.length > 0 && !selectedClassId) {
            setSelectedClassId(classList[0]);
        }
    }, [classList]);

    useEffect(() => {
        if (homeworkStats.length > 0 && !selectedHomeworkId) {
            setSelectedHomeworkId(homeworkStats[0].homeworkId.toString());
        }
    }, [homeworkStats]);

    useEffect(() => {
        if (!loading && homeworkStats.length > 0 && view === 'chart') {
            renderChart();
        }
    }, [loading, homeworkStats, view, selectedHomeworkId]);

    const renderChart = () => {
        const chartDom = chartRef.current;
        if (!chartDom) return;

        const myChart = echarts.init(chartDom);

        const option = {
            title: {
                text: '班级作业成绩统计',
                left: 'center'
            },
            tooltip: {
                trigger: 'axis',
                axisPointer: {
                    type: 'shadow'
                }
            },
            legend: {
                data: ['平均分', '最高分', '最低分'],
                top: 'bottom'
            },
            xAxis: {
                type: 'category',
                data: homeworkStats.map(item => item.title),
                axisLabel: {
                    rotate: 45,
                    interval: 0
                }
            },
            yAxis: {
                type: 'value',
                name: '分数'
            },
            series: [
                {
                    name: '平均分',
                    type: 'bar',
                    data: homeworkStats.map(item => item.averageGrade || 0)
                },
                {
                    name: '最高分',
                    type: 'bar',
                    data: homeworkStats.map(item => item.highestGrade || 0)
                },
                {
                    name: '最低分',
                    type: 'bar',
                    data: homeworkStats.map(item => item.lowestGrade || 0)
                }
            ]
        };

        myChart.setOption(option);

        return () => {
            myChart.dispose();
        };
    };

    const columns = [
        {
            title: '学生用户名',
            dataIndex: 'studentName',
            key: 'studentName',
        },
        {
            title: '成绩',
            dataIndex: 'score',
            key: 'score',
            render: (score) => score === -1 ? '未批改' : score
        },
    ];

    const selectedHomework = homeworkStats.find(item => item.homeworkId.toString() === selectedHomeworkId);

    if (error) {
        return <div>Error: {error}</div>;
    }

    const renderDetailView = () => {
        if (!selectedHomework) {
            return <Empty description="没有选中的作业" />;
        }

        const currentStudentGrades = studentGrades[selectedHomeworkId] || [];

        return (
            <Card
                title={`${selectedHomework.title} 成绩详情 (班级 ${selectedClassId})`}
                style={{ marginBottom: '20px' }}
            >
                {currentStudentGrades.length > 0 ? (
                    <Table
                        dataSource={currentStudentGrades.map(grade => ({
                            key: grade._1,
                            studentName: grade._1,
                            score: grade._2
                        }))}
                        columns={columns}
                        pagination={false}
                    />
                ) : (
                    <Empty description="暂无学生成绩数据" />
                )}
                <div style={{ marginTop: '20px' }}>
                    <p>最高分：{selectedHomework.highestGrade || '暂无数据'}</p>
                    <p>最低分：{selectedHomework.lowestGrade || '暂无数据'}</p>
                    <p>平均分：{selectedHomework.averageGrade ? selectedHomework.averageGrade.toFixed(2) : '暂无数据'}</p>
                    <p>提交人数：{selectedHomework.submittedCount || 0}</p>
                    <p>未提交人数：{selectedHomework.unsubmittedCount || 0}</p>
                </div>
            </Card>
        );
    };

    return (
        <div style={{ padding: '20px' }}>
            <h1 style={{ textAlign: 'center', marginBottom: '20px' }}>作业统计结果</h1>
            {loading ? (
                <Spin size="large" />
            ) : (
                <>
                    <Row gutter={16} style={{ marginBottom: '20px' }}>
                        <Col span={6}>
                            <Button
                                type={view === 'chart' ? 'primary' : 'default'}
                                onClick={() => setView('chart')}
                                style={{ marginRight: '10px' }}
                            >
                                成绩统计
                            </Button>
                            <Button
                                type={view === 'detail' ? 'primary' : 'default'}
                                onClick={() => setView('detail')}
                            >
                                成绩详情
                            </Button>
                        </Col>
                        <Col span={6}>
                            <Select
                                style={{ width: '100%' }}
                                value={selectedClassId}
                                onChange={(value) => {
                                    setSelectedClassId(value);
                                    setSelectedHomeworkId('');
                                }}
                                placeholder="选择班级"
                            >
                                {classList.map((classId) => (
                                    <Option key={classId} value={classId}>
                                        班级 {classId}
                                    </Option>
                                ))}
                            </Select>
                        </Col>
                        <Col span={6}>
                            <Select
                                style={{ width: '100%' }}
                                value={selectedHomeworkId}
                                onChange={setSelectedHomeworkId}
                                placeholder="选择作业"
                                disabled={!selectedClassId}
                            >
                                {homeworkStats.map((homework) => (
                                    <Option key={homework.homeworkId} value={homework.homeworkId.toString()}>
                                        {homework.title}
                                    </Option>
                                ))}
                            </Select>
                        </Col>
                    </Row>
                    {view === 'chart' ? (
                        <div ref={chartRef} style={{ width: '100%', height: '500px' }}></div>
                    ) : (
                        renderDetailView()
                    )}
                </>
            )}
        </div>
    );
};

export default AssignmentStats;