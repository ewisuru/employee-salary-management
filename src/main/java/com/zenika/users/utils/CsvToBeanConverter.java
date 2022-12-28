package com.zenika.users.utils;

import com.opencsv.bean.BeanVerifier;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.CsvToBeanFilter;
import com.opencsv.exceptions.CsvConstraintViolationException;
import com.zenika.users.exception.InvalidUserDataException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public final class CsvToBeanConverter {

  private static final String COMMENT_MARKER = "#";

  public static <T> List<T> convertCsvToBean(InputStream inputStreamCsvData, Class<T> var) {
    log.info("Converting input stream to beans");
    try (Reader reader =
        new BufferedReader(new InputStreamReader(inputStreamCsvData, StandardCharsets.UTF_8))) {
      CsvToBean<T> csvToBean = new CsvToBeanBuilder<T>(reader).withType(var).build();
      csvToBean.setFilter(getSkipCommentsFilter());
      csvToBean.setVerifiers(List.of(getBeanVerifier()));
      return csvToBean.parse();
    } catch (Exception ex) {
      log.error("Failed to convert input stream to bean", ex);
      throw new InvalidUserDataException(ex.getMessage() + " " + ex.getCause().getMessage(), ex);
    }
  }

  private static CsvToBeanFilter getSkipCommentsFilter() {
    return lineElements -> !lineElements[0].startsWith(COMMENT_MARKER);
  }

  private static <T> BeanVerifier<T> getBeanVerifier() {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    Validator validator = factory.getValidator();
    return item -> {
      Set<ConstraintViolation<Object>> violations = validator.validate(item);
      if (!violations.isEmpty()) {
        throw new CsvConstraintViolationException(
            violations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(",")));
      }
      return true;
    };
  }
}
