package com.asintoto.messagelogger.struct;

import com.asintoto.messagelogger.MessageLogger;
import com.asintoto.messagelogger.managers.Manager;
import lombok.RequiredArgsConstructor;
import revxrsal.commands.locales.LocaleReader;

import java.util.Locale;

@RequiredArgsConstructor
public class CommandMessages implements LocaleReader {

    private final MessageLogger plugin;

    @Override
    public boolean containsKey(String s) {
        return true;
    }

    @Override
    public String get(String s) {
        String res;
        switch (s) {
            case "invalid-enum": {
                res = plugin.getMessages().getString("error.invalid-enum")
                        .replace("%enum%", "{0}")
                        .replace("%content%", "{1}");
                break;
            }
            case "invalid-number": {
                res = plugin.getMessages().getString("error.invalid-number")
                        .replace("%content%", "{0}");
                break;
            }
            case "no-permission": {
                res = plugin.getMessages().getString("error.no-permission");
                break;
            }
            case "number-not-in-range": {
                res = plugin.getMessages().getString("error.number-not-in-range")
                        .replace("%value%", "{0}")
                        .replace("%min%", "{1}")
                        .replace("%max%", "{2}")
                        .replace("%content%", "{3}");
                break;
            }
            case "missing-argument": {
                res = plugin.getMessages().getString("error.missing-argument")
                        .replace("%content%", "{0}");
                break;
            }
            case "no-subcommand-specified": {
                res = plugin.getMessages().getString("error.no-subcommand-specified");
                break;
            }
            default:  {
                res = plugin.getMessages().getString("error.invalid-command")
                        .replace("%content%", "{0}");
                break;
            }
        }
        return Manager.formatMessage(res);
    }

    private final Locale locale = new Locale("en", "US");

    @Override
    public Locale getLocale() {
        return locale;
    }
}
