import SwiftUI
import Shared

func boardBackgroundColor(boardType: BoardType, colorScheme: ColorScheme) -> Color {
    let isDark = colorScheme == .dark
    switch boardType {
    case .english:
        return isDark
            ? Color(red: 0.102, green: 0.118, blue: 0.180)
            : Color(red: 0.863, green: 0.894, blue: 0.973)
    case .french:
        return isDark
            ? Color(red: 0.173, green: 0.106, blue: 0.114)
            : Color(red: 0.961, green: 0.855, blue: 0.878)
    case .german:
        return isDark
            ? Color(red: 0.165, green: 0.141, blue: 0.094)
            : Color(red: 0.961, green: 0.929, blue: 0.855)
    case .asymmetric:
        return isDark
            ? Color(red: 0.094, green: 0.165, blue: 0.133)
            : Color(red: 0.855, green: 0.941, blue: 0.910)
    case .diamond:
        return isDark
            ? Color(red: 0.165, green: 0.094, blue: 0.157)
            : Color(red: 0.941, green: 0.855, blue: 0.918)
    default:
        return isDark
            ? Color(red: 0.102, green: 0.063, blue: 0.145)
            : Color(red: 0.996, green: 0.980, blue: 1.0)
    }
}
