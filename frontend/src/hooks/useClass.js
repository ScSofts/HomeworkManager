//封装获取频道列表逻辑
import {useCallback, useEffect, useState} from "react";
import {getClassAPI, getClassStatisticsAPI} from "../apis/class";
import {useSelector} from "react-redux";
import {getStuJoinAPI} from "../apis/student";

function useClass() {
    const [classList, setClassList] = useState([]);
    const token = useSelector(state => state.user.token);
    const username = JSON.parse(window.atob(token.split('.')[1])).username;

    const fetchClassList = useCallback(async () => {
        try {
            const res = await getClassAPI(token, username);
            setClassList(res.data);
        } catch (error) {
            console.error("获取班级列表失败:", error);
        }
    }, [token, username]);

    useEffect(() => {
        fetchClassList();
    }, [fetchClassList]);

    return {
        classList,
        refetchClassList: fetchClassList
    };
}


function useGetClassStatistics(classId) {
    const token = useSelector(state => state.user.token);
    const username = JSON.parse(window.atob(token.split('.')[1])).username;

    const [homeworkStats, setHomeworkStats] = useState([]);
    const [studentGrades, setStudentGrades] = useState({});
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        const getClassStatistics = async () => {
            try {
                setLoading(true);
                const res = await getClassStatisticsAPI(token, username, classId);
                if (res.status === 200) {
                    const processedStats = res.data.map(homework => ({
                        homeworkId: homework.homework_id,
                        title: homework.title,
                        submittedCount: homework.student_grades.length,
                        unsubmittedCount: homework.unsubmitted_count,
                        totalStudents: homework.student_grades.length + homework.unsubmitted_count,
                        averageGrade: homework.student_grades.length > 0
                            ? homework.student_grades.reduce((sum, grade) => sum + (grade._2 !== -1 ? grade._2 : 0), 0) /
                            homework.student_grades.filter(grade => grade._2 !== -1).length
                            : 0,
                        highestGrade: homework.student_grades.length > 0
                            ? Math.max(...homework.student_grades.map(grade => grade._2 !== -1 ? grade._2 : -Infinity))
                            : 0,
                        lowestGrade: homework.student_grades.length > 0
                            ? Math.min(...homework.student_grades.filter(grade => grade._2 !== -1).map(grade => grade._2))
                            : 0
                    }));
                    setHomeworkStats(processedStats);

                    // 解构 student_grades
                    const gradesObj = {};
                    res.data.forEach(homework => {
                        gradesObj[homework.homework_id] = homework.student_grades;
                    });
                    setStudentGrades(gradesObj);
                } else {
                    throw new Error('Failed to fetch class statistics');
                }
            } catch (err) {
                setError(err.message);
            } finally {
                setLoading(false);
            }
        };

        if (classId) {
            getClassStatistics();
        }
    }, [token, username, classId]);

    const getOverallStats = () => {
        if (homeworkStats.length === 0) return null;

        return {
            totalHomeworks: homeworkStats.length,
            averageSubmissionRate: homeworkStats.reduce((sum, hw) => sum + (hw.submittedCount / hw.totalStudents), 0) / homeworkStats.length,
            averageGradeAcrossAll: homeworkStats.reduce((sum, hw) => sum + hw.averageGrade, 0) / homeworkStats.length
        };
    };

    return {
        homeworkStats,
        studentGrades,
        overallStats: getOverallStats(),
        loading,
        error
    };
}

function useGetStuJoinClass(){
    const [classList, setClassList] = useState([]);
    const token = useSelector(state => state.user.token);
    const username = JSON.parse(window.atob(token.split('.')[1])).username;

    const fetchJoinClassList = useCallback(async () => {
        try {
            const res = await getStuJoinAPI(token, username);
            setClassList(res.data);
        } catch (error) {
            console.error("获取班级列表失败:", error);
        }
    }, [token, username]);

    useEffect(() => {
        fetchJoinClassList();
    }, [fetchJoinClassList]);

    return {
        classList,
        refetchClassList: fetchJoinClassList
    };
}
export {useClass,useGetClassStatistics,useGetStuJoinClass}