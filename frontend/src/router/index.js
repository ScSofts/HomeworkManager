import Login from '../pages/Login'
import AdminHome from '../pages/AdminHome'
import AssignmentStats from '../pages/assignmentStats'
import CreateAssignment from '../pages/createAssignment'
import GradeAssignment from '../pages/gradeAssignment'

import { createBrowserRouter } from 'react-router-dom'
import { AuthRoute } from '../components/AuthRoute'
import Register from "../pages/Register";
import Manage from "../pages/Manage";
import Grade from "../pages/Grade";
import StuHome from "../pages/StuHome";
import ViewAssignment from "../pages/viewAssignment";
import ViewScore from "../pages/viewScore";
import SubmitAssignment from "../pages/submitAssignment";
import ManageClass from "../pages/manageClass";
import JoinClass from "../pages/joinClass";

// 配置路由实例
const router = createBrowserRouter([
    {
        path: '/',
        element: <AuthRoute><AdminHome/></AuthRoute>,
        children: [
            {
                path: '/assignmentStats',
                element: <AssignmentStats />
            },
            {
                path: '/createAssignment',
                element: <CreateAssignment />
            },
            {
                path: '/gradeAssignment',
                element: <GradeAssignment />,
                children: [
                    {
                        path:'/gradeAssignment/',
                        element: <Manage />
                    },
                    {
                        path: '/gradeAssignment/grade',
                        element: <Grade />
                    }
                    ]
            },
            {
                path:'/manageClass',
                element:<ManageClass/>
            }
        ]
    },
    {
        path: '/stuHome',
        element: <AuthRoute><StuHome /></AuthRoute>,
        children:[

            {
                path:'/stuHome/viewAssignment',
                element:<ViewAssignment />
            },
            {
                path:'/stuHome/viewScore',
                element:<ViewScore />
            },
            {
                path:'/stuHome/submitAssignment',
                element:<SubmitAssignment />
            },
            {
                path:'/stuHome/joinClass',
                element:<JoinClass/>
            }
        ]
    },
    {
        path: '/login',
        element: <Login />
    },
    {
        path:'/register',
        element: < Register />
    }
])

export default router
