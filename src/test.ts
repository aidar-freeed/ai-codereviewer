// Creates a dummy file
function createDummyFile(file: File): File {
  const dummyFile = new File([''], file.name, {
    type: file.type,
    lastModified: file.lastModified,
  });

  return dummyFile;
}