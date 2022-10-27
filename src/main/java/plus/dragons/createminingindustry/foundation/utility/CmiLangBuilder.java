package plus.dragons.createminingindustry.foundation.utility;

import com.simibubi.create.foundation.utility.LangBuilder;
import joptsimple.internal.Strings;
import net.minecraft.network.chat.MutableComponent;

import java.util.List;

public class CmiLangBuilder extends LangBuilder {
    public CmiLangBuilder(String namespace) {
        super(namespace);
    }

    @Override
    public void forGoggles(List<? super MutableComponent> tooltip, int indents) {
        tooltip.add(CmiLang.builder()
                .text(Strings.repeat(' ', 4 + indents))
                .add(this)
                .component());
    }
}
