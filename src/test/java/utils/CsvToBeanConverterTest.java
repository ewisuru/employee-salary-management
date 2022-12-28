package utils;

import com.zenika.users.dto.UsersCsvDto;
import com.zenika.users.exception.InvalidUserDataException;
import com.zenika.users.utils.CsvToBeanConverter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.io.IOException;
import java.util.List;

import static com.zenika.users.testutils.TestFileReader.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CsvToBeanConverterTest {

  private static final int EXPECTED_USER_COUNT = 8;

  @Test
  @DisplayName("Return users when input stream is valid")
  void convertCsvToBeanGivenValidInputStream() throws IOException {
    List<UsersCsvDto> usersCsvDtos =
        CsvToBeanConverter.convertCsvToBean(readFile(VALID_CSV_DATA_SOURCE), UsersCsvDto.class);
    assertEquals(EXPECTED_USER_COUNT, usersCsvDtos.size());
  }

  @Test
  @DisplayName("Throw InvalidUserDataException when input stream is invalid")
  void convertCsvToBeanGivenInvalidInputStream() {
    Executable executable =
        () ->
            CsvToBeanConverter.convertCsvToBean(
                readFile(INVALID_DATA_TYPE_CSV_DATA_SOURCE), UsersCsvDto.class);
    assertThrows(InvalidUserDataException.class, executable);
  }

  @Test
  @DisplayName("Throw InvalidUserDataException when input stream has data that fails javax validations")
  void convertCsvToBeanGivenJavaxValidationFailureOnInputStream() {
    Executable executable =
        () ->
            CsvToBeanConverter.convertCsvToBean(
                readFile(NEGATIVE_SALARY_CSV_DATA_SOURCE), UsersCsvDto.class);
    assertThrows(InvalidUserDataException.class, executable);
  }
}
