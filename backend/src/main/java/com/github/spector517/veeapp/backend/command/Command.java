package com.github.spector517.veeapp.backend.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true, fluent = true)
public class Command {

    private final String executable;
    private List<String> subcommands = new ArrayList<>();
    private List<String> args = new ArrayList<>();
    private List<Map.Entry<String, Optional<String>>> options = new ArrayList<>();
    private boolean sudo;
    private String runAs;

    public List<String> toArgList() {
        var list = new ArrayList<String>();
        if (sudo) {
            list.add("sudo");
            if (runAs != null && !runAs.isBlank()) {
                list.add("-u");
                list.add(runAs);
            }
        }
        list.add(executable);
        list.addAll(subcommands);
        list.addAll(args);
        options.forEach(entry -> {
            list.add(entry.getKey());
            entry.getValue().ifPresent(list::add);
        });
        return Collections.unmodifiableList(list);
    }

    @Override
    public String toString() {
        return String.join(" ", toArgList());
    }
}
