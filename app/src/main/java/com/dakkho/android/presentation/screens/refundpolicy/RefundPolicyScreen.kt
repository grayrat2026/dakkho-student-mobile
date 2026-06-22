package com.dakkho.android.presentation.screens.refundpolicy

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dakkho.android.presentation.components.GlassCard
import com.dakkho.android.presentation.theme.DesignToken
import com.dakkho.android.presentation.theme.SkyBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RefundPolicyScreen(
    onNavigateBack: () -> Unit = {}
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        rememberTopAppBarState()
    )

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        text = "রিফান্ড নীতি",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "ফিরে যান"
                        )
                    }
                },
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = DesignToken.Space.dp16)
        ) {
            Spacer(modifier = Modifier.height(DesignToken.Space.dp8))

            // Last updated info
            GlassCard(modifier = Modifier.fillMaxSize()) {
                Text(
                    text = "সর্বশেষ আপডেট: ১ জানুয়ারি, ২০২৫",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(DesignToken.Space.dp16)
                )
            }

            Spacer(modifier = Modifier.height(DesignToken.Space.dp16))

            // Section 1: রিফান্ড শর্তাবলী
            RefundSection(
                sectionNumber = "১",
                title = "রিফান্ড শর্তাবলী",
                paragraphs = listOf(
                    "দাক্ষ প্ল্যাটফর্মে কেনা কোর্স বা সাবস্ক্রিপশনের ক্ষেত্রে রিফান্ড পাওয়ার যোগ্য হতে হলে নিম্নলিখিত শর্ত পূরণ করতে হবে: কোর্স কেনার পর ৭ দিনের মধ্যে রিফান্ড অনুরোধ করতে হবে, কোর্সের মোট কন্টেন্টের ২৫% এর বেশি সম্পন্ন করা যাবে না, এবং কোনো সার্টিফিকেট ডাউনলোড করা যাবে না।",
                    "সাবস্ক্রিপশন ভিত্তিক সেবার ক্ষেত্রে, বিলিং সাইকেলের শুরুতে ৭ দিনের মধ্যে রিফান্ড অনুরোধ করা যাবে। বার্ষিক সাবস্ক্রিপশনের ক্ষেত্রে প্রথম ১৪ দিনের মধ্যে অনুরোধ করা যাবে যদি সেবার উল্লেখযোগ্য ব্যবহার না হয়ে থাকে।",
                    "প্রমোশনাল বা ডিসকাউন্টেড কোর্সের ক্ষেত্রে রিফান্ড মূল প্রদত্ত মূল্যের উপর ভিত্তি করে হবে, মূল মূল্যের উপর নয়। বান্ডল প্যাকেজের ক্ষেত্রে পুরো প্যাকেজের রিফান্ড প্রযোজ্য, আংশিক রিফান্ড দেওয়া হবে না।"
                )
            )

            Spacer(modifier = Modifier.height(DesignToken.Space.dp16))

            // Section 2: রিফান্ড প্রক্রিয়া
            RefundSection(
                sectionNumber = "২",
                title = "রিফান্ড প্রক্রিয়া",
                paragraphs = listOf(
                    "রিফান্ড অনুরোধ করতে অ্যাপের 'সাহায্য ও সমর্থন' বিভাগে যান অথবা support@dakkho.com এ ইমেইল করুন। আপনার অনুরোধে কোর্সের নাম, ক্রয়ের তারিখ, এবং রিফান্ডের কারণ উল্লেখ করুন। আমরা ২ কার্যদিবসের মধ্যে আপনার অনুরোধ পর্যালোচনা করব।",
                    "রিফান্ড অনুমোদিত হলে আসল পেমেন্ট পদ্ধতিতে ফেরত দেওয়া হবে। বিকাশ বা নগদের মাধ্যমে করা পেমেন্টের ক্ষেত্রে ৩-৫ কার্যদিবসে রিফান্ড প্রক্রিয়া সম্পন্ন হবে। ক্রেডিট/ডেবিট কার্ডের ক্ষেত্রে ৫-১০ কার্যদিবস লাগতে পারে।",
                    "রিফান্ড প্রক্রিয়া চলাকালীন আপনার কোর্সের অ্যাক্সেস সাময়িকভাবে সীমিত হতে পারে। রিফান্ড প্রত্যাখ্যাত হলে আপনাকে বিস্তারিত কারণ জানানো হবে এবং আপত্তি জানানোর সুযোগ থাকবে।"
                )
            )

            Spacer(modifier = Modifier.height(DesignToken.Space.dp16))

            // Section 3: সময়সীমা
            RefundSection(
                sectionNumber = "৩",
                title = "সময়সীমা",
                paragraphs = listOf(
                    "সাধারণ কোর্সের ক্ষেত্রে রিফান্ড অনুরোধের সময়সীমা ক্রয়ের তারিখ থেকে ৭ দিন। এই সময়ের মধ্যে অনুরোধ না করলে রিফান্ড পাওয়ার অধিকার বাতিল হয়ে যাবে। লাইভ ক্লাস বা ওয়ার্কশপের ক্ষেত্রে শুরুর ৪৮ ঘণ্টা আগে রিফান্ড অনুরোধ করতে হবে।",
                    "মাসিক সাবস্ক্রিপশনের ক্ষেত্রে প্রতি বিলিং সাইকেলের প্রথম ৭ দিনের মধ্যে রিফান্ড অনুরোধ করা যাবে। বার্ষিক সাবস্ক্রিপশনের ক্ষেত্রে প্রথম ১৪ দিনের মধ্যে রিফান্ড অনুরোধ করা যাবে।",
                    "বিশেষ ক্ষেত্রে যেমন প্রযুক্তিগত সমস্যায় কোর্স ব্যবহার করতে না পারলে, সময়সীমার বাইরেও রিফান্ড বিবেচনা করা হতে পারে। এই জাতীয় অনুরোধে প্রযুক্তিগত সমস্যার প্রমাণ সংযুক্ত করতে হবে।"
                )
            )

            Spacer(modifier = Modifier.height(DesignToken.Space.dp16))

            // Section 4: বাদ দেওয়া ক্ষেত্র
            RefundSection(
                sectionNumber = "৪",
                title = "বাদ দেওয়া ক্ষেত্র",
                paragraphs = listOf(
                    "নিম্নলিখিত ক্ষেত্রে রিফান্ড দেওয়া হবে না: কোর্সের ২৫% এর বেশি সম্পন্ন হলে, সার্টিফিকেট ডাউনলোড করা হলে, রিফান্ড সময়সীমা অতিক্রান্ত হলে, বা প্রমোশনাল ফ্রি কোর্সের ক্ষেত্রে। লাইভ ক্লাস শুরুর ৪৮ ঘণ্টার কম সময় বাকি থাকলেও রিফান্ড পাওয়া যাবে না।",
                    "কোর্স কন্টেন্টের মান বা শিক্ষকের শিক্ষাদান পদ্ধতির সাথে সম্মত না হওয়া রিফান্ডের কারণ হিসেবে গ্রহণযোগ্য নয়, কারণ প্রতিটি কোর্সের বিস্তারিত বিবরণ ও ফ্রি প্রিভিউ উপলব্ধ। কোর্স কেনার আগে প্রিভিউ দেখে সিদ্ধান্ত নেওয়ার পরামর্শ দেওয়া হয়।",
                    "জালিয়াতি বা অপব্যবহারের ক্ষেত্রে দাক্ষ রিফান্ড প্রত্যাখ্যান করার এবং অ্যাকাউন্ট স্থগিত করার অধিকার সংরক্ষণ করে। বারবার রিফান্ড অনুরোধ করলে আমরা সেই অ্যাকাউন্টে ভবিষ্যতে ক্রয় সীমিত করতে পারি।"
                )
            )

            Spacer(modifier = Modifier.height(DesignToken.Space.dp24))
        }
    }
}

@Composable
private fun RefundSection(
    sectionNumber: String,
    title: String,
    paragraphs: List<String>
) {
    GlassCard(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.padding(DesignToken.Space.dp16)
        ) {
            // Section Header
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(SkyBlue.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = sectionNumber,
                        color = SkyBlue,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                Spacer(modifier = Modifier.width(DesignToken.Space.dp8))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(DesignToken.Space.dp16))

            // Paragraphs
            paragraphs.forEachIndexed { index, paragraph ->
                Text(
                    text = paragraph,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 24.sp
                )
                if (index < paragraphs.lastIndex) {
                    Spacer(modifier = Modifier.height(DesignToken.Space.dp8))
                }
            }
        }
    }
}
