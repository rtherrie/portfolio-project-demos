//
//  ContentView.swift
//  SpanishChat
//
//  Created by Riana Therrien on 5/31/25.
//

import SwiftUI

struct ContentView: View {
    var body: some View {
        NavigationView {
            VStack(spacing: 0) {
                // title and flag
                VStack(spacing: 0) {
                    Text("SpanishChat")
                        .font(.system(size: 50))
                        .fontWeight(.bold)
                        .multilineTextAlignment(.center)
                    Text("🇪🇸")
                        .font(.system(size: 175))
                }
                .frame(maxWidth: .infinity) // center horizontally
                .padding(.bottom, 100)

                // navigation to topic selection
                NavigationLink(destination: TopicView()) {
                    Text("New Chat")
                        .font(.title2)
                        .frame(maxWidth: .infinity)
                        .padding()
                        .background(Color.red)
                        .foregroundColor(.white)
                        .cornerRadius(8)
                }
            }
            .padding()
            .background(Color.white)
        }
        .preferredColorScheme(.light)
    }
}

struct TopicView: View {
    @Environment(\.presentationMode) var presentationMode
    
    var topics = ["Introductions", "Food", "Travel", "Feelings", "Daily Routine", "Hobbies", "Technology", "Movies"]
    
    var body: some View {
        ScrollView {
            VStack(spacing: 20) {
                // generate topic buttons
                ForEach(0..<topics.count, id: \.self) { index in
                    NavigationLink(destination: NewChatView(chatID: index)) {
                        Text(topics[index])
                            .font(.title2)
                            .frame(width: 200)
                            .padding()
                            .background(Color.red)
                            .foregroundColor(.white)
                            .cornerRadius(8)
                    }
                }
            }
            .padding(.top, 60)
            .background(Color.white)
        }
        .background(Color.white)
        .preferredColorScheme(.light)
        .navigationTitle("Select a Chat")
        .navigationBarBackButtonHidden(true)
                .toolbar {
                    // custom back button
                    ToolbarItem(placement: .navigationBarLeading) {
                        Button(action: {
                            presentationMode.wrappedValue.dismiss()
                        }) {
                            HStack {
                                Image(systemName: "chevron.left")
                                Text("Volver")
                            }
                            .foregroundColor(.red)
                        }
                    }
                }
    }
    
}

struct Message: Identifiable {
    let id = UUID()
    let text: String
    let isUser: Bool
}

struct NewChatView: View {
    var chatID: Int

    @State private var message: String = ""
    @State private var messages: [Message] = []
    @Environment(\.presentationMode) var presentationMode

    @State private var selectedMessageID: UUID? = nil
    @State private var selectedTranslation: String? = nil

    var body: some View {
        VStack {
            ScrollViewReader { proxy in
                            ScrollView {
                                VStack {
                                    // display list of messages
                                    MessageListView(
                                        messages: messages,
                                        selectedMessageID: selectedMessageID,
                                        onSelectMessage: handleMessageSelection
                                    )
                                }
                                .padding()
                            }
                            .onChange(of: messages.count) { _, _ in
                                scrollToBottom(proxy: proxy)
                            }
                        }

            Divider()

            // input field and send button
            HStack {
                TextField("Escribe tu mensaje...", text: $message)
                    .textFieldStyle(RoundedBorderTextFieldStyle())

                Button("Enviar") {
                    sendMessage()
                }
                .padding(.horizontal)
                .padding(.vertical, 8)
                .background(Color.red)
                .foregroundColor(.white)
                .cornerRadius(8)
            }
            .padding()
        }
        .background(Color.white)
        .preferredColorScheme(.light)
        .navigationTitle("Nuevos Mensajes")
        .navigationBarBackButtonHidden(true)
        .toolbar {
            // custom back button
            ToolbarItem(placement: .navigationBarLeading) {
                Button(action: {
                    presentationMode.wrappedValue.dismiss()
                }) {
                    HStack {
                        Image(systemName: "chevron.left")
                        Text("Volver")
                    }
                    .foregroundColor(.red)
                }
            }
        }
        .overlay(
            Group {
                if let id = selectedMessageID,
                   let translation = selectedTranslation {
                    // translation popup display
                    TranslationPopupView(text: translation)
                        .frame(width: 200)
                        .cornerRadius(8)
                        .shadow(radius: 4)
                        .zIndex(1)
                        .position(x: UIScreen.main.bounds.width / 2, y: 100)
                }
            }
        )
        .onAppear {
            sendQuestion(ID: chatID)
        }
    }
    
    // handle tap to select or deselect message
    private func handleMessageSelection(_ msg: Message) {
           if selectedMessageID == msg.id {
               selectedMessageID = nil
               selectedTranslation = nil
           } else {
               selectedMessageID = msg.id
               fetchTranslation(for: msg.text)
           }
       }

    // scroll to latest message
       private func scrollToBottom(proxy: ScrollViewProxy) {
           if let lastID = messages.last?.id {
               withAnimation {
                   proxy.scrollTo(lastID, anchor: .bottom)
               }
           }
       }

    // get english translation (uses API)
    private func fetchTranslation(for spanishText: String) {
        Translate.translate(text: spanishText, from: "es", to: "en") { translatedText in
            DispatchQueue.main.async {
                selectedTranslation = translatedText
            }
        }
    }

    // send user message and get translations
    private func sendMessage() {
        let userMessage = message.trimmingCharacters(in: .whitespacesAndNewlines)
        guard !userMessage.isEmpty else { return }

        messages.append(Message(text: userMessage, isUser: true))
        message = ""
        UIApplication.shared.sendAction(#selector(UIResponder.resignFirstResponder), to: nil, from: nil, for: nil)

        Translate.translate(text: userMessage, from: "es", to: "en") { translatedText in
            messages.append(Message(text: "You said: " + translatedText, isUser: false))

            Translate.translate(text: translatedText, from: "en", to: "es") { translatedText2 in
                messages.append(Message(text: "Alternative way: " + translatedText2, isUser: false))
                sendQuestion(ID: chatID)
            }
        }
    }

    // send a random question based on topic
    private func sendQuestion(ID: Int) {
        // Questions to ask, separated by topic
        let questions: [[String]] = [
            // introductions
                ["¿Cómo te llamas?", "¿De dónde eres?", "¿Cuántos años tienes?", "¿Dónde vives?", "¿Tienes hermanos o hermanas?", "¿Cuál es tu color favorito?", "¿Qué te gusta hacer en tu tiempo libre?", "¿A qué te dedicas?", "¿Hablas otros idiomas?", "¿Cuál es tu comida favorita?", "¿Cuál es tu película favorita?", "¿Tienes mascotas?", "¿Qué música te gusta?", "¿Cuál es tu estación del año preferida?", "¿Has viajado a otro país?", "¿Qué te gusta aprender?", "¿Cuál es tu deporte favorito?", "¿Prefieres el verano o el invierno?", "¿Cómo es tu familia?", "¿Cuál es tu sueño o meta en la vida?"],
                // food
                [
                    "¿Cuál es tu comida favorita?",
                    "¿Te gusta cocinar?",
                    "¿Qué desayunas normalmente?",
                    "¿Prefieres la comida dulce o salada?",
                    "¿Has probado comida española?",
                    "¿Qué frutas te gustan más?",
                    "¿Cuál es tu restaurante favorito?",
                    "¿Qué plato sabes preparar bien?",
                    "¿Te gusta la comida picante?",
                    "¿Cuál es tu bebida preferida?",
                    "¿Qué ingredientes usas en tu plato favorito?",
                    "¿Comes comida rápida?",
                    "¿Qué postre te gusta más?",
                    "¿Has probado comida típica de otro país?",
                    "¿Qué comida no te gusta?",
                    "¿Prefieres comer en casa o en un restaurante?",
                    "¿Qué tipo de comida comes para la cena?",
                    "¿Cuál es tu comida tradicional favorita?",
                    "¿Te gusta probar comidas nuevas?",
                    "¿Cuál es tu snack favorito?"
                ],
                //travel
                [
                    "¿Has viajado a otro país?",
                    "¿Cuál fue tu último viaje?",
                    "¿Prefieres la playa o la montaña?",
                    "¿Te gusta viajar en avión o en coche?",
                    "¿Cuál es tu destino soñado?",
                    "¿Qué lugar quieres visitar en el futuro?",
                    "¿Has probado comidas nuevas cuando viajas?",
                    "¿Prefieres viajes cortos o largos?",
                    "¿Viajas solo o con amigos/familia?",
                    "¿Qué recuerdos traes de tus viajes?",
                    "¿Te gusta hacer turismo cultural?",
                    "¿Has visitado una ciudad famosa?",
                    "¿Qué llevas en tu maleta cuando viajas?",
                    "¿Qué actividades te gusta hacer cuando viajas?",
                    "¿Cuál fue el viaje más divertido que hiciste?",
                    "¿Prefieres un hotel o un hostal?",
                    "¿Has tenido problemas durante un viaje?",
                    "¿Qué idioma hablas cuando viajas?",
                    "¿Has hecho amigos en otros países?",
                    "¿Cuál es la mejor época para viajar?"
                ],
                //feelings
                [
                    "¿Cómo te sientes hoy?",
                    "¿Qué te hace feliz?",
                    "¿Qué te pone triste?",
                    "¿Qué haces cuando estás aburrido?",
                    "¿Qué te enoja?",
                    "¿Te pones nervioso antes de un examen?",
                    "¿Cómo expresas tu alegría?",
                    "¿Qué haces para relajarte?",
                    "¿Qué te da miedo?",
                    "¿Te gusta estar solo o acompañado?",
                    "¿Qué te hace sentir orgulloso?",
                    "¿Cómo reaccionas cuando estás frustrado?",
                    "¿Qué te motiva?",
                    "¿Cómo ayudas a tus amigos cuando están tristes?",
                    "¿Prefieres hablar de tus sentimientos o escribirlos?",
                    "¿Qué haces para mejorar tu ánimo?",
                    "¿Cómo te sientes cuando haces ejercicio?",
                    "¿Te gusta sorprender a las personas?",
                    "¿Cuál fue la última vez que te sentiste muy feliz?",
                    "¿Qué te hace sentir tranquilo?"
                ],
                //daily routine
                [
                    "¿A qué hora te despiertas?",
                    "¿Qué haces por la mañana?",
                    "¿Cómo es un día típico para ti?",
                    "¿A qué hora desayunas?",
                    "¿Vas al trabajo o a la escuela?",
                    "¿Qué haces después de trabajar/estudiar?",
                    "¿Cuántas horas duermes?",
                    "¿Qué haces para hacer ejercicio?",
                    "¿Qué sueles comer para el almuerzo?",
                    "¿Cuándo haces tus tareas o estudios?",
                    "¿Te gusta tu rutina diaria?",
                    "¿Qué haces los fines de semana?",
                    "¿Prefieres levantarte temprano o tarde?",
                    "¿Cuánto tiempo pasas en redes sociales?",
                    "¿Haces alguna actividad creativa?",
                    "¿A qué hora cenas?",
                    "¿Lees antes de dormir?",
                    "¿Te gusta planificar tu día?",
                    "¿Qué haces para relajarte por la noche?",
                    "¿Cuál es tu parte favorita del día?"
                ],
                //hobbies
                [
                    "¿Cuáles son tus pasatiempos favoritos?",
                    "¿Te gusta leer?",
                    "¿Practicas algún deporte?",
                    "¿Tocas algún instrumento musical?",
                    "¿Prefieres ver películas o series?",
                    "¿Te gusta pintar o dibujar?",
                    "¿Qué haces en tu tiempo libre?",
                    "¿Te gusta cocinar?",
                    "¿Juegas videojuegos?",
                    "¿Sales con amigos los fines de semana?",
                    "¿Te gusta la fotografía?",
                    "¿Qué tipo de música escuchas?",
                    "¿Has probado algún hobby nuevo recientemente?",
                    "¿Te gusta bailar?",
                    "¿Practicas yoga o meditación?",
                    "¿Te gusta hacer manualidades?",
                    "¿Tienes algún hobby que te relaje?",
                    "¿Prefieres actividades al aire libre o en casa?",
                    "¿Has viajado para practicar un hobby?",
                    "¿Cuál es el hobby que más disfrutas?"
                ],
                //technology
                [
                    "¿Qué tipo de teléfono usas?",
                    "¿Prefieres Android o iPhone?",
                    "¿Qué redes sociales usas más?",
                    "¿Te gusta jugar videojuegos?",
                    "¿Usas aplicaciones para aprender idiomas?",
                    "¿Has comprado algo en línea?",
                    "¿Qué opinas de la inteligencia artificial?",
                    "¿Te gusta la tecnología o prefieres lo tradicional?",
                    "¿Cuánto tiempo pasas frente a la pantalla?",
                    "¿Usas una computadora para trabajar o estudiar?",
                    "¿Has usado realidad virtual?",
                    "¿Prefieres leer libros digitales o en papel?",
                    "¿Qué aplicaciones usas para la productividad?",
                    "¿Te gusta la música digital o en vinilo?",
                    "¿Usas asistentes de voz como Siri o Alexa?",
                    "¿Sabes programar?",
                    "¿Cuál fue el primer dispositivo tecnológico que tuviste?",
                    "¿Confías en la seguridad de internet?",
                    "¿Qué tecnología te gustaría que existiera?",
                    "¿Cómo usas la tecnología en tu vida diaria?"
                ],
                //movies
                [
                    "¿Cuál es tu película favorita?",
                    "¿Prefieres películas de acción o comedia?",
                    "¿Vas al cine con frecuencia?",
                    "¿Te gusta ver películas en casa o en el cine?",
                    "¿Quién es tu actor o actriz favorito?",
                    "¿Cuál fue la última película que viste?",
                    "¿Te gustan las películas de terror?",
                    "¿Prefieres películas en español o en otro idioma?",
                    "¿Cuál es tu director de cine favorito?",
                    "¿Has visto alguna película española?",
                    "¿Qué género de películas te gusta más?",
                    "¿Te gusta ver series o películas?",
                    "¿Qué película te hizo llorar?",
                    "¿Cuál es la mejor película que has visto este año?",
                    "¿Te gusta ver películas con subtítulos?",
                    "¿Has visto películas clásicas?",
                    "¿Prefieres películas largas o cortas?",
                    "¿Te gusta la animación?",
                    "¿Qué película recomendarías a un amigo?",
                    "¿Con quién prefieres ver películas?"
                ]
            ]
        let randomNumber = Int.random(in: 0..<questions[ID].count)
        let question = questions[ID][randomNumber]
        messages.append(Message(text: question, isUser: false))
        message = ""
    }

    // popup view for translations
    struct TranslationPopupView: View {
        let text: String

        var body: some View {
            Text(text)
                .padding(10)
                .background(Color.white)
                .cornerRadius(8)
                .shadow(radius: 4)
                .frame(maxWidth: 200)
                .fixedSize(horizontal: false, vertical: true)
        }
    }
}

// list view of all messages
struct MessageListView: View {
    let messages: [Message]
    let selectedMessageID: UUID?
    let onSelectMessage: (Message) -> Void

    var body: some View {
        VStack(alignment: .leading, spacing: 8) {
            ForEach(messages) { msg in
                HStack {
                    if msg.isUser {
                        Spacer()
                        Text(msg.text)
                            .padding()
                            .background(Color.red.opacity(0.8))
                            .foregroundColor(.white)
                            .cornerRadius(12)
                    } else {
                        Text(msg.text)
                            .padding()
                            .background(Color.yellow.opacity(0.4))
                            .foregroundColor(.black)
                            .cornerRadius(12)
                            .onTapGesture {
                                onSelectMessage(msg)
                            }
                        Spacer()
                    }
                }
            }
        }
    }
}
    
    struct ContentView_Previews: PreviewProvider {
        static var previews: some View {
            ContentView()
        }
    }
    
