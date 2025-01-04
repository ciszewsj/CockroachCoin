import {FC, MouseEventHandler} from "react";

export const RemoveButton: FC<{ onClick?: MouseEventHandler }> = ({onClick}) => {
    return (
        <button onClick={onClick}
            className="text-white bg-red-500 hover:bg-red-600 focus:outline-none focus:ring-2 focus:ring-red-400 rounded-full w-6 h-6 flex items-center justify-center text-sm"
        >
            -
        </button>
    )
}
