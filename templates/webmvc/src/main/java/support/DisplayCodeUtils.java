package {{packageName}}.support;

import java.util.UUID;
import org.apache.commons.lang3.StringUtils;

/**
 * Implementation of 10자 노출코드, Display Code.
 *
 * @see https://wiki.mm.meshkorea.net/pages/viewpage.action?pageId=58777741
 */
public class DisplayCodeUtils {

  private static final int CONVERT_LENGTH = 2;
  private static final int DISPLAY_CODE_SPLIT_LENGTH = 4;
  private static final String UUID_DELIMITER = "-";
  private static final String DISPLAY_CODE_DELIMITER = "-";

  /**
   * @param externalId UUID
   * @return decimal value of first eight character of UUID concatenated by `-`. Each part is four characters out of
   * first eight character of UUID. Left padded with 0 to make size 5. Example externalId:
   * f30fbcfc-d6b0-4526-b734-c126332682d0 -> f30f-bcfc -> 62223-48380
   */
  public static String externalId2DisplayCode(UUID externalId) {
    if (externalId == null) {
      return null;
    }

    String target = externalId.toString().split(UUID_DELIMITER)[0]; // only take first part
    String[] parts = new String[CONVERT_LENGTH];
    for (int i = 0; i < CONVERT_LENGTH; i++) {
      int from = i * DISPLAY_CODE_SPLIT_LENGTH;
      int to = (i + 1) * DISPLAY_CODE_SPLIT_LENGTH;
      parts[i] = leftPadToMakeSizeFive(hexadecimal2decimal(target.substring(from, to)));
    }

    return combineParts(parts);
  }

  /**
   * @param code decimal representation of first eight character of UUID. example: 62223-48380
   * @return hexidecimal value of code. example: f30fbcfc
   */
  public static String convertToFirstEightCharacterOfExternalId(String code) {
    try {
      return _convertToFirstEightCharacterOfExternalId(code);
    } catch (NumberFormatException e) {
      throw new RuntimeException(code);
    }
  }

  private static String combineParts(String[] parts) {
    StringBuilder result = new StringBuilder();
    for (int i = 0; i < CONVERT_LENGTH; i++) {
      result.append(parts[i]);

      if (i < CONVERT_LENGTH - 1) {
        result.append(DISPLAY_CODE_DELIMITER);
      }
    }

    return result.toString();
  }

  private static String leftPadToMakeSizeFive(int target) {
    return String.format("%05d", target);
  }

  private static Integer hexadecimal2decimal(String hexadecimal) {
    return Integer.parseInt(hexadecimal, 16);
  }

  private static String _convertToFirstEightCharacterOfExternalId(String code) {
    if (StringUtils.isEmpty(code)) {
      return null;
    }

    String[] parts = code.split(DISPLAY_CODE_DELIMITER);

    return convertToHex(parts);
  }

  private static String convertToHex(String[] parts) {
    StringBuilder result = new StringBuilder();

    for (String part : parts) {
      int decimal = Integer.parseInt(part);
      result.append(decimal2hexdecimal(decimal));
    }

    return result.toString();
  }

  private static String leftPadToMakeSizeFour(String target) {
    return StringUtils.leftPad(target, DISPLAY_CODE_SPLIT_LENGTH, "0");
  }

  private static String decimal2hexdecimal(int decimal) {
    return leftPadToMakeSizeFour(Integer.toHexString(decimal));
  }
}
