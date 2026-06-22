package com.dakkho.android.presentation.screens.termsofservice

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
fun TermsOfServiceScreen(
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
                        text = "সেবার শর্তাবলী",
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

            // Section 1: সাধারণ শর্তাবলী
            TermsSection(
                sectionNumber = "১",
                title = "সাধারণ শর্তাবলী",
                paragraphs = listOf(
                    "দাক্ষ শিক্ষা প্ল্যাটফর্ম ব্যবহার করার মাধ্যমে আপনি এই সেবার শর্তাবলীর সাথে সম্মত হচ্ছেন। এই শর্তাবলী আপনার এবং দাক্ষ টিমের মধ্যে একটি আইনি চুক্তি গঠন করে। আপনি যদি এই শর্তাবলীর সাথে সম্মত না হন, তবে দয়া করে আমাদের সেবা ব্যবহার করবেন না।",
                    "দাক্ষ যেকোনো সময় এই শর্তাবলী পরিবর্তন করার অধিকার সংরক্ষণ করে। পরিবর্তন হলে আমরা অ্যাপের মাধ্যমে আপনাকে অবহিত করব। পরিবর্তিত শর্তাবলী প্রকাশের পর আপনি আমাদের সেবা ব্যবহার চালিয়ে গেলে তা নতুন শর্তাবলী গ্রহণের প্রমাণ হবে।",
                    "এই শর্তাবলী বাংলাদেশের আইনের অধীনে ব্যাখ্যা করা হবে এবং যেকোনো বিরোধ ঢাকার আদালতের এখতিয়ারে থাকবে।"
                )
            )

            Spacer(modifier = Modifier.height(DesignToken.Space.dp16))

            // Section 2: ব্যবহারকারীর অধিকার ও দায়িত্ব
            TermsSection(
                sectionNumber = "২",
                title = "ব্যবহারকারীর অধিকার ও দায়িত্ব",
                paragraphs = listOf(
                    "একজন ব্যবহারকারী হিসেবে আপনার অধিকার রয়েছে আমাদের প্ল্যাটফর্মের সকল শিক্ষামূলক কন্টেন্টে প্রবেশাধিকার পেতে, আপনার অ্যাকাউন্টের তথ্য আপডেট করতে এবং যেকোনো সময় আপনার অ্যাকাউন্ট মুছে ফেলার অনুরোধ করতে। আপনি আমাদের গ্রাহক সেবার মাধ্যমে যেকোনো অভিযোগ বা পরামর্শ দিতে পারেন।",
                    "আপনার দায়িত্ব হলো সঠিক তথ্য প্রদান করা, আপনার অ্যাকাউন্টের নিরাপত্তা বজায় রাখা, এবং অন্য ব্যবহারকারীদের অধিকার লঙ্ঘন না করা। আপনি কোনো অবৈধ কার্যকলাপে লিপ্ত হবেন না বা প্ল্যাটফর্মের কার্যক্রমে বাধা দেবেন না।",
                    "আপনার লগইন তথ্য অন্য কারো সাথে শেয়ার করা নিষিদ্ধ। আপনার অ্যাকাউন্টের মাধ্যমে সম্পাদিত সকল কার্যকলাপের জন্য আপনি সম্পূর্ণ দায়ী থাকবেন।"
                )
            )

            Spacer(modifier = Modifier.height(DesignToken.Space.dp16))

            // Section 3: মেধা সম্পদ
            TermsSection(
                sectionNumber = "৩",
                title = "মেধা সম্পদ",
                paragraphs = listOf(
                    "দাক্ষ প্ল্যাটফর্মের সকল কন্টেন্ট, ডিজাইন, লোগো, এবং অন্যান্য মেধা সম্পদ দাক্ষ টিমের একচ্ছত্র সম্পত্তি। কপিরাইট, ট্রেডমার্ক এবং অন্যান্য মেধা সম্পদ অধিকার আমাদের দ্বারা সংরক্ষিত। আপনি আমাদের লিখিত অনুমতি ছাড়া কোনো কন্টেন্ট পুনরুত্পাদন, বিতরণ বা পরিবর্তন করতে পারবেন না।",
                    "শিক্ষার্থীদের ব্যক্তিগত নোট এবং সৃষ্টিশীল কাজ তাদের নিজস্ব মেধা সম্পদ হিসেবে বিবেচিত হবে। তবে প্ল্যাটফর্মে আপলোড করা কন্টেন্টের মাধ্যমে আপনি দাক্ষকে সেই কন্টেন্ট প্ল্যাটফর্মে প্রদর্শন ও বিতরণের অ-একচ্ছত্র লাইসেন্স প্রদান করেন।",
                    "প্ল্যাটফর্মে প্রদত্ত শিক্ষামূলক কন্টেন্ট কেবল ব্যক্তিগত শিক্ষার উদ্দেশ্যে ব্যবহার করা যাবে। বাণিজ্যিক ব্যবহার কঠোরভাবে নিষিদ্ধ।"
                )
            )

            Spacer(modifier = Modifier.height(DesignToken.Space.dp16))

            // Section 4: পেমেন্ট ও রিফান্ড
            TermsSection(
                sectionNumber = "৪",
                title = "পেমেন্ট ও রিফান্ড",
                paragraphs = listOf(
                    "কোর্স বা সাবস্ক্রিপশন কেনার সময় আপনাকে প্রদর্শিত মূল্য প্রদান করতে হবে। সকল মূল্য বাংলাদেশি টাকায় নির্ধারিত এবং প্রযোজ্য কর অন্তর্ভুক্ত। পেমেন্ট বিকাশ, নগদ, ক্রেডিট/ডেবিট কার্ড বা অন্যান্য অনুমোদিত পদ্ধতির মাধ্যমে করা যাবে।",
                    "রিফান্ড নীতি আমাদের রিফান্ড নীতি পৃষ্ঠায় বিস্তারিতভাবে উল্লেখ আছে। সাধারণত, কোর্স শুরুর ৭ দিনের মধ্যে রিফান্ড অনুরোধ করা যাবে যদি কোর্সের ২৫% এর বেশি সম্পন্ন না হয়ে থাকে।",
                    "সাবস্ক্রিপশন স্বয়ংক্রিয়ভাবে নবায়ন হতে পারে। আপনি যেকোনো সময় স্বয়ংক্রিয় নবায়ন বাতিল করতে পারেন। বাতিলের পর বর্তমান সাবস্ক্রিপশন মেয়াদ শেষ হওয়া পর্যন্ত সেবা পাবেন।"
                )
            )

            Spacer(modifier = Modifier.height(DesignToken.Space.dp16))

            // Section 5: পরিবর্তন ও সমাপ্তি
            TermsSection(
                sectionNumber = "৫",
                title = "পরিবর্তন ও সমাপ্তি",
                paragraphs = listOf(
                    "দাক্ষ যেকোনো সময় এই শর্তাবলী পরিবর্তন করতে পারে। গুরুত্বপূর্ণ পরিবর্তনের ক্ষেত্রে আমরা কমপক্ষে ৩০ দিনের পূর্ব নোটিশ প্রদান করব। পরিবর্তিত শর্তাবলী অ্যাপে প্রকাশের তারিখ থেকে কার্যকর হবে।",
                    "আমরা যদি কোনো সেবা বন্ধ করার সিদ্ধান্ত নিই, তবে আমরা যুক্তিসঙ্গত সময়ের নোটিশ দেব এবং বাধ্যতামূলক নয় এমন সেবার ক্ষেত্রে যথাযথ রিফান্ড প্রদান করব। প্রযুক্তিগত বা নিরাপত্তাজনিত কারণে তাৎক্ষণিক সেবা বন্ধ করা লাগতে পারে।",
                    "শর্তাবলী লঙ্ঘনের ক্ষেত্রে দাক্ষ আপনার অ্যাকাউন্ট সাময়িক বা স্থায়ীভাবে স্থগিত করতে পারে। এই ধরনের পদক্ষেপের ক্ষেত্রে আমরা আপনাকে ইমেইল বা অ্যাপ নোটিফিকেশনের মাধ্যমে অবহিত করব।"
                )
            )

            Spacer(modifier = Modifier.height(DesignToken.Space.dp24))
        }
    }
}

@Composable
private fun TermsSection(
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
