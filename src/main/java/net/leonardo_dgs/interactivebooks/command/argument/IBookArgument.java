package net.leonardo_dgs.interactivebooks.command.argument;

import cloud.commandframework.ArgumentDescription;
import cloud.commandframework.arguments.CommandArgument;
import cloud.commandframework.arguments.parser.ArgumentParseResult;
import cloud.commandframework.arguments.parser.ArgumentParser;
import cloud.commandframework.context.CommandContext;
import me.lucko.helper.utils.annotation.NonnullByDefault;
import net.leonardo_dgs.interactivebooks.IBook;
import net.leonardo_dgs.interactivebooks.InteractiveBooks;
import org.bukkit.command.CommandSender;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.function.BiFunction;

@NonnullByDefault
public class IBookArgument extends CommandArgument<CommandSender, IBook> {

    public IBookArgument(boolean required,
            String name,
            String defaultValue,
            @Nullable BiFunction<CommandContext<CommandSender>, String, List<String>> suggestionsProvider,
            ArgumentDescription defaultDescription) {
        super(required, name, new Parser(), defaultValue, IBook.class, suggestionsProvider, defaultDescription);
    }

    public static IBookArgument of(final String name) {
        return builder(name).build();
    }

    public static IBookArgument optional(final String name) {
        return builder(name).asOptional().build();
    }

    public static IBookArgument.Builder builder(final String name) {
        return new IBookArgument.Builder(name);
    }

    public static final class Parser implements ArgumentParser<CommandSender, IBook> {
        @Override
        public ArgumentParseResult<IBook> parse(
                final CommandContext<CommandSender> commandContext,
                final Queue<String> inputQueue
        ) {
            String input = inputQueue.peek();
            IBook book = InteractiveBooks.getBook(input);
            if (book == null) {
                return ArgumentParseResult.failure(new IllegalArgumentException("你指定的书籍不存在"));
            }
            inputQueue.remove();
            return ArgumentParseResult.success(book);
        }

        @Override
        public List<String> suggestions(
                final CommandContext<CommandSender> commandContext,
                final String input
        ) {
            return new ArrayList<>(InteractiveBooks.getBooks().keySet());
        }
    }

    public static final class Builder extends CommandArgument.TypedBuilder<CommandSender, IBook, IBookArgument.Builder> {
        private Builder(final String name) {
            super(IBook.class, name);
        }

        @Override
        public IBookArgument build() {
            return new IBookArgument(
                    this.isRequired(),
                    this.getName(),
                    this.getDefaultValue(),
                    this.getSuggestionsProvider(),
                    this.getDefaultDescription()
            );
        }
    }

}
