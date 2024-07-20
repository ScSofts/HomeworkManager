// mockService.js

const mockStudentData = {
    name: '张三',
    id: '2021001001',
    class: '计算机科学与技术2班',
    grade: '2021级',
    major: '计算机科学与技术',
};

const mockHomeworkReminders = [
    {
        title: 'JavaScript高级特性解析',
        deadline: '2023-07-25',
        isUrgent: true
    },
    {
        title: 'React Hooks实践',
        deadline: '2023-07-30',
        isUrgent: false
    },
    {
        title: '数据结构与算法分析',
        deadline: '2023-08-05',
        isUrgent: false
    }
];

const mockAssignments = [
    {
        id: 1,
        title: '数学作业 - 微积分练习',
        publishDate: '2024-07-15',
        imageUrl: 'https://example.com/math-assignment.jpg',
        requirements: '完成课本第 123-125 页的练习题'
    },
    {
        id: 2,
        title: '语文作业 - 古文阅读',
        publishDate: '2024-07-16',
        imageUrl: null,
        requirements: '阅读《论语》前三章并写出心得体会'
    },
    {
        id: 3,
        title: '英语作业 - 听力训练',
        publishDate: '2024-07-17',
        imageUrl: 'https://example.com/english-assignment.jpg',
        requirements: '完成新概念英语第二册 Unit 1 的听力练习'
    },
    {
        id: 4,
        title: '物理作业 - 力学实验',
        publishDate: '2024-07-18',
        imageUrl: 'https://example.com/physics-assignment.jpg',
        requirements: '设计一个简单的力学实验并记录实验过程'
    },
    {
        id: 5,
        title: '化学作业 - 元素周期表',
        publishDate: '2024-07-19',
        imageUrl: null,
        requirements: '背诵元素周期表中前 20 个元素的原子序数和符号'
    }
];

const mockScores = [
    {
        id: 1,
        title: '数学作业 - 微积分练习',
        submitDate: '2024-07-16',
        requirements: '完成课本第 123-125 页的练习题',
        score: 95,
        comment: '计算过程清晰，结果准确。部分难题的解题思路很独特，继续保持。',
        submittedImageUrl: 'https://example.com/submitted-math-assignment.jpg'
    },
    {
        id: 2,
        title: '语文作业 - 古文阅读',
        submitDate: '2024-07-17',
        requirements: '阅读《论语》前三章并写出心得体会',
        score: 88,
        comment: '对文章主旨把握准确，但论述还可以更深入一些。注意标点符号的使用。',
        submittedImageUrl: null
    },
    {
        id: 3,
        title: '英语作业 - 听力训练',
        submitDate: '2024-07-18',
        requirements: '完成新概念英语第二册 Unit 1 的听力练习',
        score: 92,
        comment: '听力理解能力很好，只有少数几个难点没有准确理解。继续努力！',
        submittedImageUrl: 'https://example.com/submitted-english-assignment.jpg'
    },
];

export const fetchStudentData = () => {
    return new Promise((resolve) => {
        setTimeout(() => resolve(mockStudentData), 500);
    });
};

export const fetchHomeworkReminders = () => {
    return new Promise((resolve) => {
        setTimeout(() => resolve(mockHomeworkReminders), 500);
    });
};

export const fetchAssignments = () => {
    return new Promise((resolve) => {
        setTimeout(() => resolve(mockAssignments), 500);
    });
};

export const fetchScores = () => {
    return new Promise((resolve) => {
        setTimeout(() => resolve(mockScores), 500);
    });
};