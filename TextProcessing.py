import re
import nltk
from nltk.corpus import stopwords
from nltk.stem import PorterStemmer

def process_text(text):
    print(f"Processing text: {text}")

    clean_regex = re.compile("[^a-zA-Z0-9]")

    # rimuovo punteggiatura e caratteri != [^a-zA-Z0-9]
    text = clean_regex.sub(" ", text)

    # lowercase e split su spazi
    tokens = text.lower().split()

    print(
        f"Tokens before processing: {len(tokens)}\n"
        # printa i primi 10 token
        f"First 10 tokens: {tokens}\n"
    )

    # remove stopwords
    stop_words = set(stopwords.words('english'))
    tokens = [token for token in tokens if token not in stop_words]

    print(
        f"Tokens after removing stopwords: {len(tokens)}\n"
        # printa i primi 10 token
        f"First 10 tokens: {tokens}\n"
    )

    # stemming
    stemmer = PorterStemmer()
    tokens = [stemmer.stem(token) for token in tokens]
    print(
        f"Tokens after stemming: {len(tokens)}\n"
        # printa i primi 10 token
        f"First 10 tokens: {tokens}\n"
    )

    #token troppo lunghi <= 25
    tokens = [token for token in tokens if len(token) <= 25]
    print(
        f"Tokens after removing too long tokens: {len(tokens)}\n"
        # printa i primi 10 token
        f"First 10 tokens: {tokens}\n"
    )
    return tokens



# Esempio
if __name__ == '__main__':
    document_text = "This is an example document, containing some words for     S6      ."
    processed_tokens = process_text(document_text)
    print("Processed Tokens:", processed_tokens)
