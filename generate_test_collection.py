import tarfile
from io import TextIOWrapper
from io import BytesIO

collection_path = 'data/collection.tar'
test_collection_path = 'data/test_collection.tar'
with tarfile.open(collection_path, 'r' ) as collection:
    with tarfile.open(test_collection_path, 'w') as test_collection:
        for file_info in collection.getmembers():
            file_buffer = BytesIO(collection.extractfile(file_info).read())

            with TextIOWrapper(file_buffer, encoding='utf-8') as file_content:
                lines = [file_content.readline().strip() for _ in range(1000)]
                test_file_name = f"test_{file_info.name}"
                test_file_content = '\n'.join(lines)
                test_collection_info = tarfile.TarInfo(name=test_file_name)
                test_collection_info.size = len(test_file_content.encode('utf-8'))
                test_collection.addfile(test_collection_info, BytesIO(test_file_content.encode('utf-8')))

# printa il contenuto del nuovo archivio
with tarfile.open(test_collection_path, 'r') as test_collection:
    for file_info in test_collection.getmembers():
        print(file_info.name)
        print(test_collection.extractfile(file_info).read().decode('utf-8'))
        print()
