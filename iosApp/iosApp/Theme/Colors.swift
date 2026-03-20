import SwiftUI
import Shared

let warmBackground = Color(red: 0.961, green: 0.941, blue: 0.922)
let completedGradientStart = Color(red: 0.424, green: 0.388, blue: 1.0)
let completedGradientEnd = Color(red: 0.545, green: 0.498, blue: 1.0)
let starGold = Color(red: 0.961, green: 0.620, blue: 0.043)
let starEmpty = Color(red: 0.867, green: 0.867, blue: 0.867)
let chevronGray = Color(red: 0.800, green: 0.800, blue: 0.800)

func thumbnailGradient(boardType: BoardType) -> (start: Color, end: Color) {
    switch boardType {
    case .english:
        return (Color(red: 0.659, green: 0.847, blue: 0.918), Color(red: 0.494, green: 0.784, blue: 0.890))
    case .french:
        return (Color(red: 0.973, green: 0.706, blue: 0.784), Color(red: 0.949, green: 0.616, blue: 0.682))
    case .german:
        return (Color(red: 0.961, green: 0.902, blue: 0.792), Color(red: 0.910, green: 0.835, blue: 0.690))
    case .asymmetric:
        return (Color(red: 0.706, green: 0.902, blue: 0.784), Color(red: 0.561, green: 0.831, blue: 0.667))
    case .diamond:
        return (Color(red: 0.784, green: 0.706, blue: 0.941), Color(red: 0.690, green: 0.616, blue: 0.878))
    default:
        return (Color(red: 0.659, green: 0.847, blue: 0.918), Color(red: 0.494, green: 0.784, blue: 0.890))
    }
}
