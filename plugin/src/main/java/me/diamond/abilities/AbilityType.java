package me.diamond.abilities;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AbilityType {
    INFERNO(Inferno.class),
    SORCERER(Sorcerer.class),
    HACKER(Hacker.class);

    private final Class<? extends Ability> clazz;
}