// mockUserData.js

export const mockTeacher = {
    id: 1,
    name: "张老师",
    username: "teachera",
    avatar: "https://example.com/avatar.jpg"
};

const mockClasses = [
    { id: 1, name: "高一(1)班" },
    { id: 2, name: "高一(2)班" },
    { id: 3, name: "高二(1)班" },
    { id: 4, name: "高二(2)班" },
];

const mockStudents = [
    { id: 1, name: "张三", classId: 1 },
    { id: 2, name: "李四", classId: 1 },
    { id: 3, name: "王五", classId: 1 },
    { id: 4, name: "赵六", classId: 1 },
    { id: 5, name: "钱七", classId: 1 },
    { id: 6, name: "孙八", classId: 2 },
    { id: 7, name: "周九", classId: 2 },
    { id: 8, name: "吴十", classId: 2 },
    { id: 9, name: "郑十一", classId: 2 },
    { id: 10, name: "王十二", classId: 2 },
    { id: 11, name: "冯十三", classId: 3 },
    { id: 12, name: "陈十四", classId: 3 },
    { id: 13, name: "楚十五", classId: 3 },
    { id: 14, name: "魏十六", classId: 3 },
    { id: 15, name: "蒋十七", classId: 3 },
    { id: 16, name: "沈十八", classId: 4 },
    { id: 17, name: "韩十九", classId: 4 },
    { id: 18, name: "杨二十", classId: 4 },
    { id: 19, name: "朱二一", classId: 4 },
    { id: 20, name: "秦二二", classId: 4 },
];

const mockCourses = [
    { id: 1, name: "语文" },
    { id: 2, name: "数学" },
    { id: 3, name: "英语" },
    { id: 4, name: "物理" },
    { id: 5, name: "化学" },
];

const mockAssignments = [
    { id: 1, courseId: 1, title: "古诗文阅读", publishDate: "2023-07-01" },
    { id: 2, courseId: 2, title: "二次函数练习", publishDate: "2023-07-02" },
    { id: 3, courseId: 3, title: "听力训练", publishDate: "2023-07-03" },
    { id: 4, courseId: 4, title: "力学实验报告", publishDate: "2023-07-04" },
    { id: 5, courseId: 5, title: "化学方程式平衡", publishDate: "2023-07-05" },
    { id: 6, courseId: 1, title: "现代文阅读", publishDate: "2023-07-06" },
    { id: 7, courseId: 2, title: "立体几何", publishDate: "2023-07-07" },
    { id: 8, courseId: 3, title: "英语写作", publishDate: "2023-07-08" },
];

const generateMockAssignmentSubmissions = () => {
    let submissions = [];
    mockAssignments.forEach(assignment => {
        mockStudents.forEach(student => {
            const isSubmitted = Math.random() > 0.1;  // 90% 概率提交作业
            if (isSubmitted) {
                const isGraded = Math.random() > 0.3;  // 70% 概率已批改
                const submitDate = new Date(assignment.publishDate);
                submitDate.setDate(submitDate.getDate() + Math.floor(Math.random() * 5));
                submissions.push({
                    id: submissions.length + 1,
                    assignmentId: assignment.id,
                    studentId: student.id,
                    submitDate: submitDate.toISOString().split('T')[0],
                    status: isGraded ? '已批改' : '待批改',
                    score: isGraded ? Math.floor(Math.random() * 40) + 60 : null,
                });
            }
        });
    });
    return submissions;
};

const mockAssignmentSubmissions = generateMockAssignmentSubmissions();

export const mockFetchUserInfo = () => {
    return new Promise((resolve) => {
        setTimeout(() => {
            resolve(mockTeacher);
        }, 500);
    });
};

export const mockClearUserInfo = () => {
    return new Promise((resolve) => {
        setTimeout(() => {
            resolve({ success: true, message: "用户信息已清除" });
        }, 300);
    });
};

export const mockGetAssignments = () => {
    return new Promise((resolve) => {
        setTimeout(() => {
            const assignmentsWithDetails = mockAssignmentSubmissions.map(submission => {
                const assignment = mockAssignments.find(a => a.id === submission.assignmentId);
                const student = mockStudents.find(s => s.id === submission.studentId);
                const course = mockCourses.find(c => c.id === assignment.courseId);
                const classInfo = mockClasses.find(cls => cls.id === student.classId);
                return {
                    key: `${submission.id}`,
                    courseName: course.name,
                    className: classInfo.name,
                    assignmentTitle: assignment.title,
                    studentName: student.name,
                    assignmentStatus: submission.status,
                    submitDate: submission.submitDate,
                    score: submission.score
                };
            });
            resolve(assignmentsWithDetails);
        }, 500);
    });
};

export const mockGetClasses = () => {
    return new Promise((resolve) => {
        setTimeout(() => {
            resolve(mockClasses);
        }, 500);
    });
};

export const mockGetStatsData = () => {
    return new Promise((resolve) => {
        setTimeout(() => {
            const statsData = mockClasses.map(cls => {
                const classStudents = mockStudents.filter(student => student.classId === cls.id);
                const classSubmissions = mockAssignmentSubmissions.filter(submission =>
                    classStudents.some(student => student.id === submission.studentId) && submission.status === '已批改'
                );

                const classScores = classSubmissions.map(submission => submission.score);

                const studentAverages = classStudents.map(student => {
                    const studentSubmissions = classSubmissions.filter(submission => submission.studentId === student.id);
                    const studentScores = studentSubmissions.map(submission => submission.score);
                    const averageScore = studentScores.length > 0
                        ? Math.round(studentScores.reduce((a, b) => a + b) / studentScores.length)
                        : null;
                    return {
                        studentName: student.name,
                        score: averageScore
                    };
                });

                return {
                    className: cls.name,
                    students: studentAverages,
                    maxScore: classScores.length > 0 ? Math.max(...classScores) : null,
                    minScore: classScores.length > 0 ? Math.min(...classScores) : null,
                    avgScore: classScores.length > 0
                        ? Math.round(classScores.reduce((a, b) => a + b) / classScores.length * 10) / 10
                        : null
                };
            });
            resolve(statsData);
        }, 500);
    });
};

export const mockPublishAssignment = (formData) => {
    return new Promise((resolve) => {
        setTimeout(() => {
            resolve({ success: true, message: "作业发布成功" });
        }, 500);
    });
};

export const mockGradeAssignment = (gradeData) => {
    return new Promise((resolve) => {
        setTimeout(() => {
            resolve({ success: true, message: "批改结果提交成功" });
        }, 500);
    });
};

// 在 mockUserData.js 文件中添加以下代码

// 模拟获取待批改作业的函数
export const mockFetchPendingAssignments = () => {
    return new Promise((resolve) => {
        setTimeout(() => {
            resolve([
                {
                    id: 1,
                    title: "JavaScript高级特性解析",
                    className: "Web开发进阶班",
                    deadline: "2023-07-25",
                    submissionCount: 28
                },
                {
                    id: 2,
                    title: "React Hooks实践",
                    className: "前端框架应用班",
                    deadline: "2023-07-30",
                    submissionCount: 32
                },
                {
                    id: 3,
                    title: "数据结构与算法分析",
                    className: "计算机科学基础班",
                    deadline: "2023-08-05",
                    submissionCount: 45
                },
                {
                    id: 4,
                    title: "数据库设计与优化",
                    className: "数据库管理高级班",
                    deadline: "2023-08-10",
                    submissionCount: 20
                },
                {
                    id: 5,
                    title: "机器学习导论",
                    className: "人工智能入门班",
                    deadline: "2023-08-15",
                    submissionCount: 38
                }
            ]);
        }, 500); // 模拟网络延迟
    });
};
