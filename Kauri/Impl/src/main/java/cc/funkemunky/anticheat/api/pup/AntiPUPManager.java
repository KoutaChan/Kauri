package cc.funkemunky.anticheat.api.pup;

import cc.funkemunky.anticheat.Kauri;
import cc.funkemunky.anticheat.api.data.PlayerData;
import cc.funkemunky.anticheat.api.utils.Setting;
import cc.funkemunky.anticheat.impl.pup.bot.ConsoleClient;
import cc.funkemunky.anticheat.impl.pup.crashers.ArmSwing;
import cc.funkemunky.anticheat.impl.pup.crashers.Boxer;
import cc.funkemunky.anticheat.impl.pup.crashers.ChatSpam;
import cc.funkemunky.anticheat.impl.pup.crashers.MorePackets;
import cc.funkemunky.anticheat.impl.pup.exploits.BookEnchant;
import cc.funkemunky.api.Atlas;
import cc.funkemunky.carbon.db.Database;
import cc.funkemunky.carbon.db.DatabaseType;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AntiPUPManager {
    private List<AntiPUP> antibot = new ArrayList<>();
    public ExecutorService pupThread = Executors.newFixedThreadPool(2);
    public Database vpnCache;

    public AntiPUPManager() {
        antibot = registerMethods();
        Atlas.getInstance().getCarbon().createFlatfileDatabase(Kauri.getInstance().getDataFolder().getPath() + File.separator + "dbs", "VPN-Cache");
        vpnCache = Atlas.getInstance().getCarbon().getDatabase("VPN-Cache");
    }

    public List<AntiPUP> registerMethods() {
        List<AntiPUP> list = new ArrayList<>();

        addMethod(new ConsoleClient("ConsoleClient", PuPType.BOT, true), list);
        addMethod(new ArmSwing("ArmSwing", PuPType.CRASH, true), list);
        addMethod(new Boxer("Boxer", PuPType.CRASH, true), list);
        addMethod(new MorePackets("MorePackets", PuPType.CRASH, true), list);
        addMethod(new ChatSpam("ChatSpam", PuPType.SPAM, true), list);
        addMethod(new BookEnchant("BookEnchant", PuPType.EXPLOIT, true), list);
        //addMethod(new WorldLoader("WorldLoader", PuPType.BOT, true), list);


        for (AntiPUP pup : list) {
            Arrays.stream(pup.getClass().getDeclaredFields()).filter(field -> {
                field.setAccessible(true);

                return field.isAnnotationPresent(Setting.class);
            }).forEach(field -> {
                try {
                    field.setAccessible(true);

                    String path = "antipup." + pup.getName() + ".settings." + field.getName();
                    if (Kauri.getInstance().getConfig().get(path) != null) {
                        Object val = Kauri.getInstance().getConfig().get(path);

                        if (val instanceof Double && field.get(pup) instanceof Float) {
                            field.set(pup, (float) (double) val);
                        } else {
                            field.set(pup, val);
                        }
                    } else {
                        Kauri.getInstance().getConfig().set("antipup." + pup.getName() + ".settings." + field.getName(), field.get(pup));
                        Kauri.getInstance().saveConfig();
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            });

            if (Kauri.getInstance().getConfig().get("antipup." + pup.getName() + ".enabled") == null) {
                Kauri.getInstance().getConfig().set("antipup." + pup.getName() + ".enabled", pup.isEnabled());
            } else {
                pup.setEnabled(Kauri.getInstance().getConfig().getBoolean("antipup." + pup.getName() + ".enabled"));
            }
        }

        return list;
    }

    public void addMethod(AntiPUP pup, List<AntiPUP> list) {
        list.add(pup);
    }

    public AntiPUP getMethodByName(String name) {
        Optional<AntiPUP> opPUP = antibot.stream().filter(pup -> pup.getName().equalsIgnoreCase(name)).findFirst();

        return opPUP.orElse(null);
    }

    public void loadMethodsIntoData(PlayerData data) {
        List<AntiPUP> methodList = registerMethods();

        for (AntiPUP antiPUP : methodList) {
            antiPUP.setData(data);
            data.getAntiPUP().add(antiPUP);
        }
    }
}
