import re
import tarfile
from TextProcessing import process_text
from io import TextIOWrapper, BytesIO

#
# ogni riga è cosi composta <pid>\t<text>\n dove <pid> è un numero intero e <text> è una stringa
# ottieni una lista di tuple (pid, text) dove pid è un intero e text è una stringa
#

def get_pid_text_list(collection_path):
    pid_text_list = []
    with tarfile.open(collection_path, 'r') as collection:
         file = collection.getmembers()[0]
         file_buffer = BytesIO(collection.extractfile(file).read())

         with TextIOWrapper(file_buffer, encoding='utf-8', errors='replace' ) as file_content:
            for line in file_content:

                try:
                    # Extracting doc_no and document from the line
                    pid, doc = line.strip().split('\t')
                except ValueError:
                    print(f"Malformed line: {line}")
                    continue

                # check text vuoto
                if not doc.strip():
                    print(f"Skipping empty text for pid {pid}")
                    continue

                # check malformed pid
                try:
                    pid = int(pid) # try converting pid to an integer
                except ValueError:
                    print(f"Skipping malformed pid {pid}")
                    continue

                token_list = process_text(doc)

                pid_text_list.append((int(pid), token_list))


    return pid_text_list

if __name__ == '__main__':
    collection_path = 'data/test_collection.tar'
    pid_doc_list = get_pid_text_list(collection_path)
    print(pid_doc_list[:10])
