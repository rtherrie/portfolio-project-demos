//
//  ContentView.swift
//  QuestNote
//
//  Created by Riana Therrien on 6/3/25.
//

import SwiftUI

// entry structure (codable so it can save)
struct DiaryEntry: Identifiable, Codable {
    let id: UUID
    let game: String
    let date: String
    let entryText: String
    
    init(game: String, date: String, entryText: String) {
        self.id = UUID()
        self.game = game
        self.date = date
        self.entryText = entryText
    }
}

struct ContentView: View {
    @StateObject private var data = Data()
    
    @State private var trophyScale: CGFloat = 7.0
    @State private var rotation = 0.0
    @State private var showTitle = false
    @State private var showButton = false

    var body: some View {
        NavigationView {
            ZStack {
                Color.black.ignoresSafeArea() // background color
                
                VStack(spacing: 20) {
                    ZStack {
                        Image(systemName: "trophy") // trophy icon
                            .resizable()
                            .scaledToFit()
                            .frame(width: 100, height: 100)
                            .scaleEffect(trophyScale)
                            .rotationEffect(.degrees(rotation))
                            .foregroundColor(.white)
                            .onAppear {
                                // animate size and rotation
                                withAnimation(.easeOut(duration: 1.5)) {
                                    trophyScale = 3
                                }
                                withAnimation(.linear(duration: 1.5)) {
                                    rotation = 360
                                }
                                // show title and button with delay
                                DispatchQueue.main.asyncAfter(deadline: .now() + 1.4) {
                                    withAnimation(.easeIn) {
                                        showTitle = true
                                    }
                                }
                                DispatchQueue.main.asyncAfter(deadline: .now() + 1.8) {
                                    withAnimation(.easeIn) {
                                        showButton = true
                                    }
                                }
                            }
                    }
                    .frame(height: 140)
                    
                    Spacer()
                    
                    if showTitle {
                        Text("Quest Note") // app title
                            .font(.system(size: 50, weight: .bold))
                            .foregroundColor(.blue)
                            .transition(.opacity)
                            .padding(.bottom, 50)
                    }
                    
                    if showButton {
                        NavigationLink(destination: GameView().environmentObject(data)) {
                            Text("Start") // start button
                                .padding(15)
                                .background(Color.blue)
                                .foregroundColor(.white)
                                .cornerRadius(10)
                                .transition(.move(edge: .bottom).combined(with: .opacity))
                        }
                    }

                    Spacer()
                }
                .padding(.top, 200)
            }
        }
    }
}


// data for app
// holds games and entries with save/load
class Data: ObservableObject {
    @Published var games: [String] = [] {
        didSet { saveGames() }
    }
    @Published var entries: [DiaryEntry] = [] {
        didSet { saveEntries() }
    }
    
    private let gamesKey = "games_key"
    private let entriesKey = "entries_key"

    init() {
        loadGames()
        loadEntries()
    }

    private func saveGames() {
        UserDefaults.standard.set(games, forKey: gamesKey)
    }

    private func loadGames() {
        games = UserDefaults.standard.stringArray(forKey: gamesKey) ?? []
    }

    private func saveEntries() {
        if let encoded = try? JSONEncoder().encode(entries) {
            UserDefaults.standard.set(encoded, forKey: entriesKey)
        }
    }

    private func loadEntries() {
        if let savedData = UserDefaults.standard.data(forKey: entriesKey),
           let decoded = try? JSONDecoder().decode([DiaryEntry].self, from: savedData) {
            entries = decoded
        }
    }
}


struct GameView: View {
    @EnvironmentObject var data: Data
    @State private var gameToDelete: String? = nil
    @State private var showDeleteGameAlert = false

    var body: some View {
        ZStack {
            Color.appBackground.ignoresSafeArea() // background

            VStack {
                Text("Games") // title
                    .font(.title)
                    .padding(.bottom, 15)
                    .foregroundColor(.white)

                List {
                    ForEach(data.games, id: \.self) { item in
                        ZStack {
                            RoundedRectangle(cornerRadius: 10)
                                .fill(Color.blue)

                            NavigationLink(destination: NotesView(gameName: item).environmentObject(data)) {
                                Text(item)
                                    .padding()
                                    .font(.title)
                                    .fontWeight(.bold)
                                    .foregroundColor(.white)
                            }
                        }
                        .padding(.vertical, 4)
                        .listRowBackground(Color.clear)
                    }
                    .onDelete { indexSet in
                        if let index = indexSet.first {
                            gameToDelete = data.games[index]
                            showDeleteGameAlert = true
                        }
                    }
                }
                .scrollContentBackground(.hidden)
                .background(Color.clear)
                .alert("Delete Game?", isPresented: $showDeleteGameAlert, presenting: gameToDelete) { game in
                    Button("Delete", role: .destructive) {
                        if let index = data.games.firstIndex(of: game) {
                            data.games.remove(at: index)
                        }
                    }
                    Button("Cancel", role: .cancel) {}
                } message: { game in
                    Text("Are you sure you want to delete \"\(game)\"?")
                }
            }
            .padding()
        }
        .toolbar {
            // navigation buttons
            ToolbarItemGroup(placement: .navigationBarTrailing) {
                NavigationLink(destination: CalendarView().environmentObject(data)) {
                    Image(systemName: "calendar")
                        .foregroundColor(.blue)
                }

                NavigationLink(destination: AddGameView().environmentObject(data)) {
                    Image(systemName: "plus")
                        .foregroundColor(.blue)
                }
            }
        }
    }
}




struct AddGameView: View {
    // Environment Object
    @EnvironmentObject var data: Data
    
    @State private var newGameName = ""

        var body: some View {
            VStack(spacing: 20) {
                Text("Add Game")  // title
                    .font(.title)
                    .padding(.bottom, 50)
                    .foregroundColor(.white)

                TextField("Enter game name", text: $newGameName)
                    .textFieldStyle(RoundedBorderTextFieldStyle())
                    .foregroundColor(Color.blue)
                    .padding()

                Button("Add") {
                    guard !newGameName.isEmpty else { return }
                    data.games.append(newGameName)
                    newGameName = ""
                }
                .padding()
                .background(Color.blue)
                .foregroundColor(.white)
                .cornerRadius(10)

                Spacer()
            }
            .padding()
            .background(Color.appBackground)
        }
    }

struct NotesView: View {
    let gameName: String
    @EnvironmentObject var data: Data
    
    @State private var entryToDelete: DiaryEntry? = nil
    @State private var showDeleteEntryAlert = false

    var gameEntries: [DiaryEntry] {
        data.entries
            .filter { $0.game == gameName }
            .sorted { $0.date > $1.date }
    }

    var body: some View {
        VStack{
            Text("Notes for \(gameName)")  // title
                .font(.title)
                .padding(.bottom, 15)
                .foregroundColor(.white)

            List {
                ForEach(gameEntries) { entry in
                    NoteRowView(entry: entry)
                        .listRowBackground(Color.blue)
                }
                .onDelete { indexSet in
                    if let index = indexSet.first {
                        entryToDelete = gameEntries[index]
                        showDeleteEntryAlert = true
                    }
                }
            }
            .scrollContentBackground(.hidden)
            .background(Color.appBackground)
            .alert("Delete Entry?", isPresented: $showDeleteEntryAlert, presenting: entryToDelete) { entry in
                Button("Delete", role: .destructive) {
                    if let index = data.entries.firstIndex(where: { $0.id == entry.id }) {
                        data.entries.remove(at: index)
                    }
                }
                Button("Cancel", role: .cancel) {}
            } message: { entry in
                Text("Are you sure you want to delete this entry from \(entry.date)?")
            }
        }
        .padding()
        .toolbar {
            // calendar and add note buttons
            ToolbarItemGroup(placement: .navigationBarTrailing) {
                NavigationLink(destination: CalendarView().environmentObject(data)) {
                    Image(systemName: "calendar")
                        .foregroundColor(.blue)
                }

                NavigationLink(destination: AddNoteView(gameName: gameName).environmentObject(data)) {
                    Image(systemName: "plus")
                        .foregroundColor(.blue)
                }
            }
        }
        .background(Color.appBackground)
    }
}

struct NoteRowView: View {
    let entry: DiaryEntry

    var body: some View {
        VStack(alignment: .leading, spacing: 5) {
            Text(entry.date)
                .font(.caption)
                .foregroundColor(Color.black)

            Text(entry.entryText)
                .foregroundColor(Color.white)
                .lineLimit(nil) // allow full text
        }
        .padding()
        .frame(maxWidth: .infinity, alignment: .leading) // make standard Width
        .background(Color.blue)
        .cornerRadius(10)
        .listRowBackground(Color.clear)
    }
}



struct AddNoteView: View {
    let gameName: String
    @EnvironmentObject var data: Data

    @State private var acomplishedText = ""
    @State private var goalText = ""

    var body: some View {
        VStack(spacing: 20) {
            Text("Add Entry")
                .font(.title)
                .padding(.bottom, 50)
                .foregroundColor(.white)

            VStack(alignment: .leading) {
                Text("What did you accomplish in this session?")
                    .foregroundColor(.white)
                    .padding(.bottom, 5)

                TextEditor(text: $acomplishedText)
                    .frame(height: 120) // taller box
                    .padding(8)
                    .background(Color(white: 0.15))
                    .cornerRadius(8)
                    .foregroundColor(.blue)
                    .overlay(
                        RoundedRectangle(cornerRadius: 8)
                            .stroke(Color.gray.opacity(0.5))
                    )
            }
            .padding(.horizontal)

            VStack(alignment: .leading) {
                Text("What is your goal for next session?")
                    .foregroundColor(.white)
                    .padding(.bottom, 5)

                TextEditor(text: $goalText)
                    .frame(height: 120) // taller box
                    .padding(8)
                    .background(Color(white: 0.15))
                    .cornerRadius(8)
                    .foregroundColor(.blue)
                    .overlay(
                        RoundedRectangle(cornerRadius: 8)
                            .stroke(Color.gray.opacity(0.5))
                    )
            }
            .padding(.horizontal)

            Button("Add") {
                let newEntry = "This session: \n" + acomplishedText + "\n\nNext Session Goal(s):\n" + goalText
                guard !acomplishedText.isEmpty && !goalText.isEmpty else { return }

                let formatter = DateFormatter()
                formatter.dateStyle = .medium
                formatter.timeStyle = .short
                let formattedDate = formatter.string(from: Date())

                let entry = DiaryEntry(game: gameName, date: formattedDate, entryText: newEntry)
                data.entries.append(entry)

                // move game to front
                if let idx = data.games.firstIndex(of: gameName) {
                    data.games.remove(at: idx)
                }
                data.games.insert(gameName, at: 0)

                acomplishedText = ""
                goalText = ""
            }
            .padding()
            .background(Color.blue)
            .foregroundColor(.white)
            .cornerRadius(10)

            Spacer()
        }
        .padding()
        .background(Color.appBackground)
    }
}

struct CalendarDay: Identifiable {
    let id = UUID()
    let date: Date
    let isWithinDisplayedMonth: Bool
}

import SwiftUI

struct CalendarView: View {
    @EnvironmentObject var data: Data
    
    @State private var displayedMonth = Date()
    @State private var selectedDate: Date? = nil
    @State private var showEntriesSheet = false


    private var calendar: Calendar { Calendar.current }
    private var dateFormatter: DateFormatter {
        let f = DateFormatter()
        f.dateFormat = "MMMM yyyy"
        return f
    }

    // Convert string dates to Date
    private var entryDates: Set<Date> {
        let formatter = DateFormatter()
        formatter.dateStyle = .medium
        formatter.timeStyle = .short
        
        return Set(data.entries.compactMap {
            guard let fullDate = formatter.date(from: $0.date) else { return nil }
            return calendar.startOfDay(for: fullDate)
        })
    }

    var body: some View {
        ZStack{
            Color.appBackground
                        .ignoresSafeArea()  // fills entire screen
        VStack {
            // month navigation
            HStack {
                Button(action: {
                    displayedMonth = calendar.date(byAdding: .month, value: -1, to: displayedMonth)!
                }) {
                    Image(systemName: "chevron.left")
                }
                
                Spacer()
                
                Text(dateFormatter.string(from: displayedMonth))
                    .font(.headline)
                
                Spacer()
                
                Button(action: {
                    displayedMonth = calendar.date(byAdding: .month, value: 1, to: displayedMonth)!
                }) {
                    Image(systemName: "chevron.right")
                }
            }
            .padding()
            .foregroundColor(.white)
            
            // days of the week header
            let weekdaySymbols = calendar.shortStandaloneWeekdaySymbols
            HStack {
                ForEach(weekdaySymbols, id: \.self) { day in
                    Text(day)
                        .font(.caption)
                        .frame(maxWidth: .infinity)
                        .foregroundColor(.gray)
                }
            }
            
            // days Grid
            let days = generateCalendarDays(for: displayedMonth)
            LazyVGrid(columns: Array(repeating: .init(.flexible()), count: 7)) {
                ForEach(days) { day in
                    Button(action: {
                        if entryDates.contains(calendar.startOfDay(for: day.date)) {
                            selectedDate = day.date
                        }
                    }) {
                        VStack {
                            Text("\(calendar.component(.day, from: day.date))")
                                .foregroundColor(day.isWithinDisplayedMonth ? .white : .gray)
                            
                            if entryDates.contains(calendar.startOfDay(for: day.date)) {
                                Circle()
                                    .fill(Color.blue)
                                    .frame(width: 6, height: 6)
                            } else {
                                Spacer().frame(height: 6)
                            }
                        }
                        .frame(height: 40)
                    }
                    .buttonStyle(PlainButtonStyle())
                    .frame(height: 40)
                }
            }
        }
        .padding()
    }
        .background(Color.appBackground)
        .sheet(isPresented: $showEntriesSheet) {
            EntrySheetView(entries: selectedEntries)
                .presentationDetents([.medium, .large])
        }
        .onChange(of: selectedDate) { oldValue, newValue in
            if newValue != nil {
                DispatchQueue.main.async {
                    showEntriesSheet = true
                }
            }
        }
    }


    // generate days to fill calendar grid
    private func generateCalendarDays(for month: Date) -> [CalendarDay] {
        guard let monthInterval = calendar.dateInterval(of: .month, for: month),
              let firstWeek = calendar.dateInterval(of: .weekOfMonth, for: monthInterval.start),
              let lastWeek = calendar.dateInterval(of: .weekOfMonth, for: monthInterval.end - 1)
        else {
            return []
        }

        let days = stride(from: firstWeek.start, through: lastWeek.end, by: 86400).map { date -> CalendarDay in
            let isCurrentMonth = calendar.isDate(date, equalTo: month, toGranularity: .month)
            return CalendarDay(date: date, isWithinDisplayedMonth: isCurrentMonth)
        }

        return days
    }
    
    private var selectedEntries: [DiaryEntry] {
        guard let selected = selectedDate else { return [] }
        let dayStart = calendar.startOfDay(for: selected)
        
        return data.entries.filter {
            if let entryDate = dateFormatterWithTime.date(from: $0.date) {
                return calendar.isDate(entryDate, inSameDayAs: dayStart)
            }
            return false
        }
    }

    private var dateFormatterWithTime: DateFormatter {
        let f = DateFormatter()
        f.dateStyle = .medium
        f.timeStyle = .short
        return f
    }
}

struct EntrySheetView: View {
    let entries: [DiaryEntry]

    var body: some View {
        NavigationView {
            List(entries) { entry in
                VStack(alignment: .leading, spacing: 5) {
                    Text(entry.game)
                        .font(.headline)
                    Text(entry.date)
                        .font(.caption)
                        .foregroundColor(.black)
                    Divider()
                        .background(Color.white.opacity(0.5))
                    Text(entry.entryText)
                        .fixedSize(horizontal: false, vertical: true)
                }
                .padding()
                .background(Color.blue)
                .cornerRadius(10)
                .foregroundColor(.white)
                .listRowBackground(Color.clear) // clear default white background
            }
            .scrollContentBackground(.hidden) // removes default list background
            .background(Color.black) // overall list background
            .navigationTitle("Entries")
        }
    }
}


#Preview {
    ContentView()
}
