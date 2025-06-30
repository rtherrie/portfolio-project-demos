import Foundation

struct Translate {
    // API functions to translate user input text
    static func translate(text: String, from sourceLang: String = "en", to targetLang: String = "es", completion: @escaping (String) -> Void) {
        let baseURL = "https://ftapi.pythonanywhere.com/translate"
        guard var components = URLComponents(string: baseURL) else {
            completion("Invalid URL")
            return
        }

        components.queryItems = [
            URLQueryItem(name: "sl", value: sourceLang),
            URLQueryItem(name: "dl", value: targetLang),
            URLQueryItem(name: "text", value: text)
        ]

        guard let url = components.url else {
            completion("Failed to create request URL")
            return
        }

        URLSession.shared.dataTask(with: url) { data, response, error in
            if let error = error {
                DispatchQueue.main.async {
                    completion("Network error: \(error.localizedDescription)")
                }
                return
            }

            guard let data = data else {
                DispatchQueue.main.async {
                    completion("No data received")
                }
                return
            }

            do {
                if let json = try JSONSerialization.jsonObject(with: data) as? [String: Any],
                   let translatedText = json["destination-text"] as? String {
                    DispatchQueue.main.async {
                        completion(translatedText)
                    }
                } else {
                    DispatchQueue.main.async {
                        completion("Invalid response format")
                    }
                }
            } catch {
                DispatchQueue.main.async {
                    completion("Error decoding response: \(error.localizedDescription)")
                }
            }
        }.resume()
    }
}
