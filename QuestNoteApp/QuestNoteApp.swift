//
//  QuestNoteApp.swift
//  QuestNote
//
//  Created by Riana Therrien on 6/3/25.
//

import SwiftUI

@main
struct QuestNoteApp: App {
    init() {
        let navBarAppearance = UINavigationBarAppearance()
        navBarAppearance.configureWithOpaqueBackground()
        navBarAppearance.backgroundColor = UIColor(Color.appBackground) // your custom bg color
        navBarAppearance.titleTextAttributes = [.foregroundColor: UIColor.white]
        navBarAppearance.largeTitleTextAttributes = [.foregroundColor: UIColor.white]

        UINavigationBar.appearance().standardAppearance = navBarAppearance
        UINavigationBar.appearance().scrollEdgeAppearance = navBarAppearance
        UINavigationBar.appearance().compactAppearance = navBarAppearance

        UINavigationBar.appearance().tintColor = .white  // This sets back button and nav buttons to white
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
