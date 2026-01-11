
import ApiService from "./ApiService.jsx";
import {Navigate, useLocation} from "react-router-dom";


export const CustomerRoute = ({element: Component}) =>{
    const location = useLocation()
    return ApiService.isAdmin()? (Component) : (<Navigate to={"/login"} replace state={{from: location}}/>)
}

export const AdminRoute = ({element: Component}) =>{
    const location = useLocation()
    return ApiService.isAdmin()? (Component) : (<Navigate to={"/login"} replace state={{from: location}}/>)
}