package kr.aling.file.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * File 저장 하는 위치 Enum.
 *
 * <ol>
 *     <li>Local</li>
 *     <li>Object Storage(NHN Cloud)</li>
 * </ol>
 *
 * @author : 박경서
 * @since : 1.0
 **/
@Getter
@RequiredArgsConstructor
public enum FileSaveLocation {
    LOCAL("LOCAL"), OBJECT_STORAGE("OBJECT_STORAGE");

    private final String name;
}
