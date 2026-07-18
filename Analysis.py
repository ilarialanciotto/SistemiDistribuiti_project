from flask import Flask, request, jsonify
from multi_rake import Rake
import spacy

app = Flask(__name__)

nlp = spacy.load("it_core_news_md")

LEXICONS = {
    "database": ["database", "sql", "query", "postgres", "mysql", "oracle", "tabella", "indice", "migrazione", "lock"],
    "network": ["rete", "network", "wifi", "router", "internet", "connessione", "switch", "ip", "vlan", "gateway",
                "ping", "sfp", "loop", "sottorete"],
    "bug": ["bug", "crash", "errore", "eccezione", "nullpointerexception", "fail", "inaspettatamente", "blocco",
            "termina"],
    "configuration": ["configurazione", "setup", "impostazione", "installazione", "permesso", "credenziali", "account",
                      "profilo"]
}

@app.route('/api/nlp/analyze', methods=['POST'])
def analyze_ticket_category():
    data = request.json
    description = data.get("description", "")

    if not description.strip():
        return jsonify({"category": "other"})

    ticket_doc = nlp(description)

    scores = {cat: 0 for cat in LEXICONS}

    for token in ticket_doc:
        lemma = token.lemma_.lower()
        text_lower = token.text.lower()

        for cat_name, words in LEXICONS.items():
            if lemma in words or text_lower in words:
                scores[cat_name] += 1

    best_category = max(scores, key=scores.get)
    if scores[best_category] == 0:
        best_category = "other"

    return jsonify({
        "category": best_category,
    })

@app.route('/api/nlp/analyzeTicket', methods=['POST'])
def analyze_ticket():
    data = request.json
    description = data.get("description", "")

    if not description.strip():
        return jsonify({"keywords": "", "category": "other", "priority_level": 3})

    ticket_doc = nlp(description)

    valid_keywords = [
        token.text for token in ticket_doc
        if token.pos_ in ["NOUN", "PROPN"] and not token.is_stop and len(token.text) > 2
    ]
    keywords = ",".join(list(dict.fromkeys(valid_keywords))[:5])
    scores = {cat: 0 for cat in LEXICONS}

    for token in ticket_doc:
        lemma = token.lemma_.lower()
        text_lower = token.text.lower()

        for cat_name, words in LEXICONS.items():
            if lemma in words or text_lower in words:
                scores[cat_name] += 1

    best_category = max(scores, key=scores.get)
    if scores[best_category] == 0:
        best_category = "other"

    priority = 3
    ticket_lemmas = [token.lemma_.lower() for token in ticket_doc]
    critical_indicators = ["bloccare", "crash", "loop", "fallire", "down", "irraggiungibile"]
    high_indicators = ["urgente", "grave", "subito", "asap", "interruzione", "errore"]
    low_indicators = ["lentezza", "lento", "notare", "sembrare", "ottimizzazione"]
    info_indicators = ["informazione", "chiarimento", "domanda", "guida", "grazie"]

    if any(lemma in critical_indicators for lemma in ticket_lemmas):
        priority = 5
    elif any(lemma in high_indicators for lemma in ticket_lemmas):
        priority = 4
    elif any(lemma in low_indicators for lemma in ticket_lemmas):
        priority = 2
    elif any(lemma in info_indicators for lemma in ticket_lemmas):
        priority = 1

    return jsonify({
        "keywords": keywords,
        "category": best_category,
        "priority_level": priority
    })

if __name__ == '__main__':
    app.run(port=5000)