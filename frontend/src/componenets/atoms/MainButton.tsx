import {FC, MouseEventHandler} from "react";

export const MainButton: FC<{
    children?: string | undefined
    onClick?: MouseEventHandler<any> | undefined,
}> = ({onClick, children}) => {
    return (
        <button
            type="submit"
            className="w-full px-4 py-2 text-white bg-blue-600 rounded-lg hover:bg-blue-700 focus:ring-4 focus:ring-blue-300"
            onClick={onClick}
        >
            {children}
        </button>
    )
}
