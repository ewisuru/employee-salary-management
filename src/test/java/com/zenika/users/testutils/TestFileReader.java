package com.zenika.users.testutils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public final class TestFileReader {

  public static final String VALID_CSV_DATA_SOURCE = "src/test/resources/valid_users_upload_file.csv";
  public static final String INVALID_DATA_TYPE_CSV_DATA_SOURCE = "src/test/resources/invalid_data_type_users_upload_file.csv";
  public static final String NEGATIVE_SALARY_CSV_DATA_SOURCE = "src/test/resources/negative_salary_users_upload_file.csv";
  public static final String DUPLICATE_ID_CSV_DATA_SOURCE = "src/test/resources/duplicate_id_users_upload_file.csv";

  public static InputStream readFile(String src) throws IOException {
    return Files.newInputStream(Paths.get(src));
  }
}
