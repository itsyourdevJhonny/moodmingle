package com.emc.moodmingle.ui.dailymood.page

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.emc.moodmingle.data.firebase.model.post.dailymood.DailyMoodEntity
import com.emc.moodmingle.ui.theme.GrayTextColor
import com.emc.moodmingle.ui.theme.PrimaryDark
import com.emc.moodmingle.ui.theme.Typography
import com.emc.moodmingle.utils.components.ScaffoldHeader

@Composable
fun DailyMoodSecondPage(
    mood: DailyMoodEntity,
    onNextPage: () -> Unit,
    onShowMoodDialog: (Boolean) -> Unit,
    onMoodSelected: (emoji: String, description: String) -> Unit,
    onBack: () -> Unit,
) {
    BackHandler { onBack() }

    Scaffold(
        containerColor = Color.Black,
        topBar = {
            ScaffoldHeader(
                doneLabel = "Change Mood",
                enabled = true,
                onDone = { onShowMoodDialog(true) }) {
                onBack()
            }
        },
        floatingActionButton = { SkipButton(onNextPage) }
    ) { paddingValues ->
        Content(paddingValues, mood, onMoodSelected)
    }
}

@Composable
private fun Content(
    paddingValues: PaddingValues,
    mood: DailyMoodEntity,
    onMoodSelected: (emoji: String, description: String) -> Unit,
) {
    val moodQuotes = getMoodQuotes().filter { it.first == mood.mood.emoji }

    Column(
        modifier = Modifier
            .padding(paddingValues)
            .fillMaxSize()
    ) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(16.dp)
        ) {
            items(items = moodQuotes, key = { it.second }) { (emoji, quote) ->
                Row(
                    modifier = Modifier
                        .clickable { onMoodSelected(emoji, quote) }
                        .fillMaxWidth()
                        .border(
                            width = 1.dp,
                            color = GrayTextColor,
                            shape = RoundedCornerShape(16.dp)
                        )
                        .padding(vertical = 8.dp, horizontal = 12.dp)
                        .animateItem(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(text = emoji, fontSize = 20.sp, color = Color.White)
                    Text(text = quote, style = Typography.labelLarge, color = Color.White)
                }
            }
        }
    }
}

@Composable
private fun SkipButton(onNextPage: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.CenterEnd
    ) {
        Row(
            modifier = Modifier
                .clickable { onNextPage() }
                .background(PrimaryDark, CircleShape)
                .padding(8.dp)
        ) {

            Text(text = " Skip ", color = Color.White, fontWeight = FontWeight.Bold)

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "Skip",
                tint = Color.White
            )
        }
    }
}

fun getMoodQuotes(): List<Pair<String, String>> {
    return listOf(
        // 😀 Happy
        "😀" to "Happiness is not something ready-made. It comes from your own actions. – Dalai Lama",
        "😀" to "The best way to cheer yourself is to try to cheer someone else up. – Mark Twain",
        "😀" to "Happiness is a direction, not a place. – Sydney J. Harris",
        "😀" to "Be happy for this moment. This moment is your life. – Omar Khayyam",
        "😀" to "Happiness depends upon ourselves. – Aristotle",

        // 😌 Calm
        "😌" to "Peace is not absence of conflict, it is the ability to cope with it. – Unknown",
        "😌" to "Calmness is the cradle of power. – Josiah Gilbert Holland",
        "😌" to "In the midst of movement and chaos, keep stillness inside of you. – Deepak Chopra",
        "😌" to "The quieter you become, the more you can hear. – Ram Dass",
        "😌" to "Serenity is not freedom from the storm, but peace amid the storm. – Unknown",

        // 🤗 Excited
        "🤗" to "Excitement is the more practical synonym for happiness, and it is precisely what you should strive to chase. – Tim Ferriss",
        "🤗" to "Life is either a daring adventure or nothing at all. – Helen Keller",
        "🤗" to "The only way to do great work is to love what you do. – Steve Jobs",
        "🤗" to "Adventure is out there! – Up (movie)",
        "🤗" to "Get excited about the possibilities. – Unknown",

        // 🙏 Grateful
        "🙏" to "Gratitude turns what we have into enough. – Anonymous",
        "🙏" to "Be thankful for what you have; you'll end up having more. – Oprah Winfrey",
        "🙏" to "Gratitude is the fairest blossom which springs from the soul. – Henry Ward Beecher",
        "🙏" to "When you are grateful, fear disappears and abundance appears. – Tony Robbins",
        "🙏" to "Thankfulness is the beginning of gratitude. – Irish Proverb",

        // 😬 Anxious
        "😬" to "Anxiety is like a rocking chair. It gives you something to do, but it doesn't get you very far. – Jodi Picoult",
        "😬" to "You don't have to control your thoughts. You just have to stop letting them control you. – Dan Millman",
        "😬" to "The way out of anxiety is to go through it, not around it. – Unknown",
        "😬" to "Worry does not empty tomorrow of its sorrow; it empties today of its strength. – Corrie ten Boom",
        "😬" to "Anxiety's like a rocking chair. It gives you something to do, but it doesn't get you very far. – Glenn Turner",

        // 😢 Sad
        "😢" to "Tears are words that need to be written. – Paulo Coelho",
        "😢" to "Sadness flies away on the wings of time. – Jean de La Fontaine",
        "😢" to "The word 'happy' would lose its meaning if it were not balanced by sadness. – Carl Jung",
        "😢" to "It's okay to feel sad. It's part of being human. – Unknown",
        "😢" to "Sadness is but a wall between two gardens. – Kahlil Gibran",

        // 😠 Angry
        "😠" to "Anger is an acid that can do more harm to the vessel in which it is stored than to anything on which it is poured. – Mark Twain",
        "😠" to "Holding on to anger is like grasping a hot coal with the intent of throwing it at someone else; you are the one who gets burned. – Buddha",
        "😠" to "For every minute you remain angry, you give up sixty seconds of peace of mind. – Ralph Waldo Emerson",
        "😠" to "Anger is never without a reason, but seldom with a good one. – Benjamin Franklin",
        "😠" to "The best fighter is never angry. – Lao Tzu",

        // 😴 Sleepy
        "😴" to "A good laugh and a long sleep are the best cures in the doctor's book. – Irish Proverb",
        "😴" to "Sleep is the best meditation. – Dalai Lama",
        "😴" to "Early to bed and early to rise makes a man healthy, wealthy, and wise. – Benjamin Franklin",
        "😴" to "There is a time for many words, and there is also a time for sleep. – Homer",
        "😴" to "Sleep is that golden chain that ties health and our bodies together. – Thomas Dekker",

        // 🤔 Thoughtful
        "🤔" to "The unexamined life is not worth living. – Socrates",
        "🤔" to "Thinking is the hardest work there is, which is probably the reason why so few engage in it. – Henry Ford",
        "🤔" to "The mind is everything. What you think you become. – Buddha",
        "🤔" to "Thoughts are the shadows of our feelings – always darker, emptier and simpler. – Friedrich Nietzsche",
        "🤔" to "To think is to live. – Cicero",

        // 😳 Embarrassed
        "😳" to "Embarrassment is the feeling you get when you realize you have just made a fool of yourself. – Unknown",
        "😳" to "The only way to avoid being embarrassed is to never do anything. – Unknown",
        "😳" to "Embarrassment is a villain to be crushed. – Robert A. Heinlein",
        "😳" to "Blushing is the color of virtue. – Diogenes",
        "😳" to "It's okay to be embarrassed; it's part of growing up. – Unknown",

        // 😇 Content
        "😇" to "Contentment is natural wealth, luxury is artificial poverty. – Socrates",
        "😇" to "He who is contented is rich. – Lao Tzu",
        "😇" to "Contentment comes not so much from great wealth as from few wants. – Epictetus",
        "😇" to "The greatest wealth is to live content with little. – Plato",
        "😇" to "Contentment is the only real wealth. – Alfred Nobel",

        // 🤩 Amazed
        "🤩" to "The world is full of magic things, patiently waiting for our senses to grow sharper. – W.B. Yeats",
        "🤩" to "Wonder is the beginning of wisdom. – Socrates",
        "🤩" to "The most beautiful thing we can experience is the mysterious. – Albert Einstein",
        "🤩" to "Look at everything always as though you were seeing it either for the first or last time. – Betty Smith",
        "🤩" to "The universe is full of magical things patiently waiting for our wits to grow sharper. – Eden Phillpotts",

        // 🥰 Loved
        "🥰" to "Love is composed of a single soul inhabiting two bodies. – Aristotle",
        "🥰" to "To love and be loved is to feel the sun from both sides. – David Viscott",
        "🥰" to "Love is not just looking at each other, it's looking in the same direction. – Antoine de Saint-Exupéry",
        "🥰" to "The best thing to hold onto in life is each other. – Audrey Hepburn",
        "🥰" to "Love is the greatest refreshment in life. – Pablo Picasso",

        // 😭 Heartbroken
        "😭" to "The heart was made to be broken. – Oscar Wilde",
        "😭" to "Sometimes good things fall apart so better things can fall together. – Marilyn Monroe",
        "😭" to "It's better to have loved and lost than never to have loved at all. – Alfred Lord Tennyson",
        "😭" to "The pain of parting is nothing to the joy of meeting again. – Charles Dickens",
        "😭" to "Hearts will never be practical until they are made unbreakable. – The Wizard of Oz",

        // 😎 Confident
        "😎" to "Confidence is the most beautiful thing you can possess. – Sabrina Carpenter",
        "😎" to "Believe you can and you're halfway there. – Theodore Roosevelt",
        "😎" to "With confidence, you have won before you have started. – Marcus Garvey",
        "😎" to "Self-confidence is the first requisite to great undertakings. – Samuel Johnson",
        "😎" to "Confidence comes not from always being right but from not fearing to be wrong. – Peter T. Mcintyre",

        // 😕 Confused
        "😕" to "Confusion is the welcome mat at the door of creativity. – Michael J. Gelb",
        "😕" to "The most confused we ever get is when we're trying to convince our heads of something our heart knows is a lie. – Karen Marie Moning",
        "😕" to "In the middle of difficulty lies opportunity. – Albert Einstein",
        "😕" to "Clarity comes with simplicity. – Unknown",
        "😕" to "Sometimes the questions are complicated and the answers are simple. – Dr. Seuss",

        // 😮 Surprised
        "😮" to "Surprise is the greatest gift which life can grant us. – Boris Pasternak",
        "😮" to "The best things in life are unexpected. – Eli Khamarov",
        "😮" to "Life is full of surprises, some good, some not so good. – Unknown",
        "😮" to "Expect the unexpected. – Unknown",
        "😮" to "Surprises are like gifts; they come in all shapes and sizes. – Unknown",

        // 😒 Bored
        "😒" to "Boredom is the feeling that everything is a waste of time; serenity, that nothing is. – Thomas Szasz",
        "😒" to "The cure for boredom is curiosity. There is no cure for curiosity. – Dorothy Parker",
        "😒" to "Boredom is just the reverse side of fascination. – A. A. Milne",
        "😒" to "If you're bored with life, if you don't get up every morning with a burning desire to do things, you don't have enough goals. – Lou Holtz",
        "😒" to "Boredom is a sign that you're not living up to your potential. – Unknown",

        // 😤 Frustrated
        "😤" to "Frustration is the first step towards improvement. – Unknown",
        "😤" to "The best way out is always through. – Robert Frost",
        "😤" to "Patience is the companion of wisdom. – Saint Augustine",
        "😤" to "Frustration, although quite painful at times, is a very positive and essential part of success. – Bo Bennett",
        "😤" to "When one door closes, another opens; but we often look so long and so regretfully upon the closed door that we do not see the one which has opened for us. – Alexander Graham Bell",

        // 🤒 Sick
        "🤒" to "Health is not valued till sickness comes. – Thomas Fuller",
        "🤒" to "The greatest wealth is health. – Virgil",
        "🤒" to "It is health that is real wealth and not pieces of gold and silver. – Mahatma Gandhi",
        "🤒" to "Take care of your body. It's the only place you have to live. – Jim Rohn",
        "🤒" to "A healthy outside starts from the inside. – Robert Urich",

        // 🤪 Playful
        "🤪" to "Play is the highest form of research. – Albert Einstein",
        "🤪" to "We don't stop playing because we grow old; we grow old because we stop playing. – George Bernard Shaw",
        "🤪" to "The creation of something new is not accomplished by the intellect but by the play instinct. – Carl Jung",
        "🤪" to "Play is our brain's favorite way of learning. – Diane Ackerman",
        "🤪" to "Life is more fun if you play games. – Roald Dahl",

        // 😞 Disappointed
        "😞" to "Disappointment is a sort of bankruptcy – the bankruptcy of a soul that expends too much in hope and expectation. – Eric Hoffer",
        "😞" to "When one door of happiness closes, another opens; but often we look so long at the closed door that we do not see the one which has been opened for us. – Helen Keller",
        "😞" to "Disappointments are to the soul what a thunderstorm is to the air. – Friedrich Schiller",
        "😞" to "The size of your success is measured by the strength of your desire; the size of your dream; and how you handle disappointment along the way. – Robert Kiyosaki",
        "😞" to "Disappointment is just the action of your brain readjusting itself to reality after discovering things are not the way you thought they were. – Unknown",

        // 🥳 Cheerful
        "🥳" to "A cheerful heart is good medicine, but a crushed spirit dries up the bones. – Proverbs 17:22",
        "🥳" to "Cheerfulness is the best promoter of health and is as friendly to the mind as to the body. – Joseph Addison",
        "🥳" to "Be cheerful while you are alive. – Ptahhotep",
        "🥳" to "Cheerfulness keeps up a kind of daylight in the mind. – Joseph Addison",
        "🥳" to "A merry heart does good like a medicine. – Proverbs 17:22",

        // 🤯 Overwhelmed
        "🤯" to "You must do the thing you think you cannot do. – Eleanor Roosevelt",
        "🤯" to "The only way to make sense out of change is to plunge into it, move with it, and join the dance. – Alan Watts",
        "🤯" to "When you feel overwhelmed, remember: a step at a time is all it takes. – Unknown",
        "🤯" to "Overwhelm is the result of too much input without enough processing time. – Unknown",
        "🤯" to "Take a deep breath and start from the beginning. – Unknown",

        // 🥺 Hopeful
        "🥺" to "Hope is being able to see that there is light despite all of the darkness. – Desmond Tutu",
        "🥺" to "Hope is the thing with feathers that perches in the soul. – Emily Dickinson",
        "🥺" to "Keep your hopes up. – Unknown",
        "🥺" to "Hope is a waking dream. – Aristotle",
        "🥺" to "Where there is hope, there is life. – Unknown",

        // 😔 Lonely
        "😔" to "Loneliness is not lack of company, loneliness is lack of purpose. – Guillermo Maldonado",
        "😔" to "The greatest thing you'll ever learn is just to love and be loved in return. – Eden Ahbez",
        "😔" to "Loneliness expresses the pain of being alone and solitude expresses the glory of being alone. – Paul Tillich",
        "😔" to "If you are lonely when you're alone, you are in bad company. – Jean-Paul Sartre",
        "😔" to "The cure for loneliness is solitude. – Marianne Moore",

        // 😱 Scared
        "😱" to "Courage is not the absence of fear, but rather the assessment that something else is more important than fear. – Franklin D. Roosevelt",
        "😱" to "Fear is stupid. So are regrets. – Marilyn Monroe",
        "😱" to "The only thing we have to fear is fear itself. – Franklin D. Roosevelt",
        "😱" to "Do one thing every day that scares you. – Eleanor Roosevelt",
        "😱" to "Fear defeats more people than any other one thing in the world. – Ralph Waldo Emerson",

        // 🤫 Secretive
        "🤫" to "Three may keep a secret, if two of them are dead. – Benjamin Franklin",
        "🤫" to "Secrets are things we give to others to keep for us. – Elbert Hubbard",
        "🤫" to "The secret of getting ahead is getting started. – Mark Twain",
        "🤫" to "A secret is powerful when it is empty. – Umberto Eco",
        "🤫" to "Secrets are made to be found out with time. – Charles Sanford",

        // 😐 Neutral
        "😐" to "In the middle of every difficulty lies opportunity. – Albert Einstein",
        "😐" to "Balance is not something you find, it's something you create. – Jana Kingsford",
        "😐" to "Neutrality is at times a graver sin than belligerence. – Louis D. Brandeis",
        "😐" to "The best way to predict the future is to create it. – Peter Drucker",
        "😐" to "Sometimes the most important thing in a whole day is the rest we take between two deep breaths. – Etty Hillesum",

        // 🫠 Exhausted
        "🫠" to "Rest when you're weary. Refresh and renew yourself, your body, your mind, your spirit. Then get back to work. – Ralph Marston",
        "🫠" to "Sometimes the most productive thing you can do is rest. – Unknown",
        "🫠" to "Exhaustion is not a status symbol. – Unknown",
        "🫠" to "You can't pour from an empty cup. Take care of yourself first. – Unknown",
        "🫠" to "Rest is not idleness, and to lie sometimes on the grass under trees on a summer's day, listening to the murmur of the water, or watching the clouds float across the sky, is by no means a waste of time. – John Lubbock"
    )
}