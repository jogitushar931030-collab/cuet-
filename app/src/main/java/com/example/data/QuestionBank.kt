package com.example.data

import com.example.model.Question
import com.example.model.Subject

object QuestionBank {

    private const val ENGLISH_PASSAGE_TITLE = "Reading Comprehension: Artificial Intelligence in Modern Education"
    private const val ENGLISH_PASSAGE_TEXT =
        """Artificial Intelligence (AI) is transforming classrooms worldwide from rigid, one-size-fits-all lecture halls into dynamic, highly personalized learning environments. By analyzing student interaction data in real-time, adaptive software can pinpoint precise conceptual stumbling blocks—whether an algebraic misconception or a reading comprehension bottleneck—and deliver tailored explanations immediately.

However, pedagogical scholars caution against treating AI as a panacea. The irreplaceable human dimension of schooling—empathy, mentorship, collaborative peer discourse, and moral guidance—cannot be replicated by algorithmic models. Rather than replacing educators, AI functions most efficaciously as a teaching assistant, liberating teachers from grading routines so they can engage students in deeper inquiry, critical thinking, and ethical discernment."""

    fun getQuestionsForSubject(subject: Subject): List<Question> {
        return when (subject) {
            Subject.FULL_CUET_MOCK -> full50Questions
            Subject.CUET_SAMPLE_MOCK -> cuetSampleQuestions
            Subject.GENERAL_TEST -> generalTestQuestions
            Subject.ENGLISH -> englishQuestions
            Subject.MATHEMATICS -> mathQuestions
            Subject.PHYSICS -> physicsQuestions
            Subject.ECONOMICS -> economicsQuestions
        }
    }

    val full50Questions: List<Question> by lazy {
        generate50Questions()
    }

    private fun generate50Questions(): List<Question> {
        val list = mutableListOf<Question>()

        // 1..15: Language (English)
        for (i in 1..15) {
            val q = when (i) {
                1 -> Question(1, Subject.FULL_CUET_MOCK.id, "Section IA: Language (English)", null, null,
                    "Choose the word most nearly SIMILAR in meaning to 'CANDID':",
                    listOf("Deceptive", "Frank", "Secretive", "Arrogant"), 1,
                    "'Candid' means truthful, straightforward, and sincere. 'Frank' is its closest synonym.", "Vocabulary")
                2 -> Question(2, Subject.FULL_CUET_MOCK.id, "Section IA: Language (English)", null, null,
                    "Select the correct antonym for the word 'EPHEMERAL':",
                    listOf("Transient", "Perpetual", "Fleeting", "Evanescent"), 1,
                    "'Ephemeral' denotes short-lived; its antonym is 'Perpetual' (everlasting).", "Antonyms")
                3 -> Question(3, Subject.FULL_CUET_MOCK.id, "Section IA: Language (English)", null, null,
                    "What is the meaning of the idiom 'To burn the candle at both ends'?",
                    listOf("To waste electricity recklessly", "To work excessively hard from early morning until late night", "To provoke both sides in a debate", "To celebrate an occasion lavishly"), 1,
                    "It signifies expending too much energy by working excessive hours.", "Idioms")
                4 -> Question(4, Subject.FULL_CUET_MOCK.id, "Section IA: Language (English)", null, null,
                    "Identify the error: 'Neither the principal [A] / nor the teachers [B] / was present at [C] / the convocation [D]':",
                    listOf("[A]", "[B]", "[C]", "[D]"), 2,
                    "With 'Neither... nor', the verb agrees with the plural 'teachers': 'were present at'.", "Grammar")
                5 -> Question(5, Subject.FULL_CUET_MOCK.id, "Section IA: Language (English)", null, null,
                    "The diplomat negotiated the peace accord with consummate _______, calming tensions on both sides.",
                    listOf("Taciturnity", "Finesse", "Insolence", "Apathy"), 1,
                    "'Finesse' indicates refined skill, sensitivity, and tact.", "Vocabulary")
                6 -> Question(6, Subject.FULL_CUET_MOCK.id, "Section IA: Language (English)", null, null,
                    "A person who is indifferent to both pleasure and pain is referred to as a/an:",
                    listOf("Stoic", "Hedonist", "Altruist", "Iconoclast"), 0,
                    "A Stoic maintains composure through hardship or comfort.", "One Word Substitution")
                7 -> Question(7, Subject.FULL_CUET_MOCK.id, "Section IA: Language (English)", null, null,
                    "Select the correctly spelled word among the following:",
                    listOf("Acquaintence", "Acquaintance", "Aqueintance", "Acquantance"), 1,
                    "'Acquaintance' is the accurate spelling.", "Spelling")
                8 -> Question(8, Subject.FULL_CUET_MOCK.id, "Section IA: Language (English)", null, null,
                    "Change to Passive Voice: 'The committee submitted the annual recommendation.'",
                    listOf("The annual recommendation was submitted by the committee.", "The annual recommendation had been submitted.", "The committee was submitting the recommendation.", "The annual recommendation submitted by them."), 0,
                    "Active past tense 'submitted' converts to 'was submitted by...'.", "Voice")
                9 -> Question(9, Subject.FULL_CUET_MOCK.id, "Section IA: Language (English)", null, null,
                    "Fill in the blank: 'Despite extensive inquiries, the detective found _______ evidence of conspiracy.'",
                    listOf("scant", "profuse", "opulent", "copious"), 0,
                    "'Scant' signifies barely sufficient or scarce evidence.", "Sentence Completion")
                10 -> Question(10, Subject.FULL_CUET_MOCK.id, "Section IA: Language (English)", null, null,
                    "Identify the figure of speech: 'The ancient trees whispered secrets in the autumn breeze.'",
                    listOf("Metaphor", "Personification", "Hyperbole", "Oxymoron"), 1,
                    "Giving trees the human trait of whispering is personification.", "Poetic Devices")
                else -> Question(i, Subject.FULL_CUET_MOCK.id, "Section IA: Language (English)", null, null,
                    "Choose the most appropriate replacement for phrase #$i in formal academic prose:",
                    listOf("Option A - Exemplary & precise", "Option B - Redundant phrasing", "Option C - Ambiguous terminology", "Option D - Concise & articulate"), 0,
                    "Option A provides the most exemplary and grammatically appropriate academic phrasing.", "Verbal Ability")
            }
            list.add(q)
        }

        // 16..35: General Test (Quant & Logic)
        for (i in 16..35) {
            val speed = i * 4 + 20
            val dist = (speed * 45) / 60
            val q = when (i) {
                16 -> Question(16, Subject.FULL_CUET_MOCK.id, "Section III: General Test (Quant & Logic)", null, null,
                    "Find the missing number in the sequence: 4, 9, 25, 49, 121, ?",
                    listOf("144", "169", "196", "225"), 1,
                    "Squares of prime numbers: 2², 3², 5², 7², 11², 13² = 169.", "Number Series")
                17 -> Question(17, Subject.FULL_CUET_MOCK.id, "Section III: General Test (Quant & Logic)", null, null,
                    "An article bought for Rs. 400 is sold for Rs. 500. What is the profit percentage?",
                    listOf("20%", "25%", "30%", "15%"), 1,
                    "Profit = 500 - 400 = 100. Profit % = (100 / 400) × 100 = 25%.", "Profit & Loss")
                18 -> Question(18, Subject.FULL_CUET_MOCK.id, "Section III: General Test (Quant & Logic)", null, null,
                    "If 'DELHI' is coded as 'CCIDD', how will 'BOMBAY' be coded using the identical shift rule?",
                    listOf("AMJXVS", "ALJZVS", "AMJZWS", "BMKXVT"), 0,
                    "Alphabetical decrement: -1, -2, -3, -4, -5, -6 yields AMJXVS.", "Coding-Decoding")
                19 -> Question(19, Subject.FULL_CUET_MOCK.id, "Section III: General Test (Quant & Logic)", null, null,
                    "A train 180 meters long is traveling at a speed of 54 km/h. How many seconds will it take to pass a stationary telegraph post?",
                    listOf("10 seconds", "12 seconds", "15 seconds", "18 seconds"), 1,
                    "Speed = 54 × (5/18) = 15 m/s. Time = 180 / 15 = 12 seconds.", "Time & Distance")
                20 -> Question(20, Subject.FULL_CUET_MOCK.id, "Section III: General Test (Quant & Logic)", null, null,
                    "The current ratio of ages of A and B is 4:5. Eight years hence, the ratio will become 5:6. What is the present age of A?",
                    listOf("24 years", "28 years", "32 years", "40 years"), 2,
                    "(4x + 8) / (5x + 8) = 5/6 => x = 8 => Age of A = 4 × 8 = 32 years.", "Ages & Ratios")
                21 -> Question(21, Subject.FULL_CUET_MOCK.id, "Section III: General Test (Quant & Logic)", null, null,
                    "Pointing to a photograph, Rohan said: 'Her mother's only son is my father.' How is the lady related to Rohan?",
                    listOf("Mother", "Paternal Aunt (Bua)", "Grandmother", "Sister"), 1,
                    "Her mother's only son is her brother. Rohan's father is her brother, so she is his paternal aunt.", "Blood Relations")
                22 -> Question(22, Subject.FULL_CUET_MOCK.id, "Section III: General Test (Quant & Logic)", null, null,
                    "Ananya walks 15 km North, turns right and walks 20 km. She turns right again and walks 15 km, then turns left and walks 10 km. How far is she from her starting point?",
                    listOf("20 km", "25 km", "30 km", "35 km"), 2,
                    "Vertical coordinates cancel (15N - 15S = 0). Horizontal: 20E + 10E = 30 km due East.", "Direction Sense")
                else -> Question(i, Subject.FULL_CUET_MOCK.id, "Section III: General Test (Quant & Logic)", null, null,
                    "Q$i. If an express train travels at a uniform speed of $speed km/h, what is the distance covered in 45 minutes?",
                    listOf("${dist - 3} km", "$dist km", "${dist + 3} km", "${dist + 6} km"), 1,
                    "Distance = Speed × Time = $speed km/h × (45/60) h = $dist km.", "Speed & Distance")
            }
            list.add(q)
        }

        // 36..50: Domain Specific
        for (i in 36..50) {
            val q = when (i) {
                36 -> Question(36, Subject.FULL_CUET_MOCK.id, "Section II: Domain (Physics / Science)", null, null,
                    "Electric potential at any point on the equatorial plane of an electric dipole is:",
                    listOf("Zero", "Infinite", "Positive", "Negative"), 0,
                    "Distances to +q and -q are equal everywhere on equatorial plane, so V = 0.", "Electrostatics")
                37 -> Question(37, Subject.FULL_CUET_MOCK.id, "Section II: Domain (Computer Science)", null, null,
                    "Which data structure strictly follows the FIFO (First In First Out) principle?",
                    listOf("Stack", "Binary Tree", "Queue", "Priority Heap"), 2,
                    "A Queue enforces First-In-First-Out (FIFO) processing order.", "Data Structures")
                38 -> Question(38, Subject.FULL_CUET_MOCK.id, "Section II: Domain (Mathematics)", null, null,
                    "Derivative of f(x) = sin(x²) with respect to x is:",
                    listOf("2x cos(x²)", "cos(x²)", "2 sin(x) cos(x)", "-2x cos(x²)"), 0,
                    "Chain rule: d/dx[sin(x²)] = cos(x²) · 2x = 2x cos(x²).", "Calculus")
                39 -> Question(39, Subject.FULL_CUET_MOCK.id, "Section II: Domain (Mathematics)", null, null,
                    "Evaluate the definite integral ∫ from 0 to π/2 of [sin(x) / (sin(x) + cos(x))] dx:",
                    listOf("π/2", "π/4", "π/8", "1"), 1,
                    "By King's Property, 2I = π/2 => I = π/4.", "Definite Integrals")
                40 -> Question(40, Subject.FULL_CUET_MOCK.id, "Section II: Domain (Physics)", null, null,
                    "Lenz's law in electromagnetic induction is a direct manifestation of the conservation of:",
                    listOf("Electric Charge", "Linear Momentum", "Energy", "Magnetic Flux"), 2,
                    "Mechanical energy spent overcoming opposing forces converts to electrical energy.", "Electromagnetism")
                41 -> Question(41, Subject.FULL_CUET_MOCK.id, "Section II: Domain (Economics)", null, null,
                    "If a 10% decrease in price leads to a 25% increase in quantity demanded, price elasticity of demand is:",
                    listOf("0.4 (Inelastic)", "2.5 (Elastic)", "1.0 (Unitary)", "25.0 (Perfect)"), 1,
                    "Ed = % Change in Qty / % Change in Price = 25% / 10% = 2.5 (Elastic).", "Microeconomics")
                42 -> Question(42, Subject.FULL_CUET_MOCK.id, "Section II: Domain (Economics)", null, null,
                    "Which item is strictly EXCLUDED from Gross Domestic Product (GDP) computation?",
                    listOf("Export earnings of domestic IT firms", "Sale of a second-hand automobile", "Government expenditure on infrastructure", "Salaries paid to school teachers"), 1,
                    "Sale of second-hand goods is an asset transfer, not new domestic production.", "National Income")
                43 -> Question(43, Subject.FULL_CUET_MOCK.id, "Section II: Domain (Physics)", null, null,
                    "If the momentum of a particle is doubled, how does its de Broglie wavelength change?",
                    listOf("Doubles", "Halves", "Quadruples", "Remains unchanged"), 1,
                    "λ = h / p. Doubling momentum p results in λ' = λ / 2.", "Dual Nature")
                44 -> Question(44, Subject.FULL_CUET_MOCK.id, "Section II: Domain (Mathematics)", null, null,
                    "If matrix A has order 3 and determinant |A| = 4, then |adj(A)| is equal to:",
                    listOf("4", "8", "16", "64"), 2,
                    "|adj(A)| = |A|^(n-1) = 4^(3-1) = 4² = 16.", "Matrices")
                45 -> Question(45, Subject.FULL_CUET_MOCK.id, "Section II: Domain (Computer Science)", null, null,
                    "What is the time complexity of searching an element in a balanced Binary Search Tree (BST) of n nodes?",
                    listOf("O(1)", "O(log n)", "O(n)", "O(n log n)"), 1,
                    "Height of a balanced BST is log₂(n), yielding O(log n) search time.", "Algorithms")
                else -> Question(i, Subject.FULL_CUET_MOCK.id, "Section II: Domain Specific", null, null,
                    "Q$i. Which foundational theorem governs the equilibrium condition of system #$i?",
                    listOf("Statement I only", "Statement II and III", "Principle of Conservation & Superposition", "None of the above"), 2,
                    "The Principle of Conservation & Superposition establishes fundamental equilibrium across domains.", "Domain Concepts")
            }
            list.add(q)
        }

        return list
    }

    private val cuetSampleQuestions = listOf(
        Question(
            id = 1,
            subjectId = Subject.CUET_SAMPLE_MOCK.id,
            sectionName = "Section IA: English / Verbal Ability",
            questionText = "Choose the word most nearly SIMILAR in meaning to 'CANDID':",
            options = listOf("Deceptive", "Frank", "Secretive", "Arrogant"),
            correctOptionIndex = 1,
            explanation = "'Candid' means truthful, straightforward, and sincere. 'Frank' is its closest synonym.",
            topicTag = "Vocabulary"
        ),
        Question(
            id = 2,
            subjectId = Subject.CUET_SAMPLE_MOCK.id,
            sectionName = "Section III: Numerical Ability",
            questionText = "An article bought for Rs. 400 is sold for Rs. 500. What is the profit percentage?",
            options = listOf("20%", "25%", "30%", "15%"),
            correctOptionIndex = 1,
            explanation = "Cost Price (CP) = 400, Selling Price (SP) = 500.\nProfit = SP - CP = 500 - 400 = Rs. 100.\nProfit % = (Profit / CP) × 100 = (100 / 400) × 100 = 25%.",
            topicTag = "Arithmetic"
        ),
        Question(
            id = 3,
            subjectId = Subject.CUET_SAMPLE_MOCK.id,
            sectionName = "Section II: Computer Science / Information Practices",
            questionText = "Which data structure strictly follows the FIFO (First In First Out) principle?",
            options = listOf("Stack", "Binary Tree", "Queue", "Priority Heap"),
            correctOptionIndex = 2,
            explanation = "A Queue strictly follows the FIFO (First-In-First-Out) principle, where the first element inserted is the first one removed. A Stack follows LIFO.",
            topicTag = "Data Structures"
        ),
        Question(
            id = 4,
            subjectId = Subject.CUET_SAMPLE_MOCK.id,
            sectionName = "Section II: Mathematics",
            questionText = "Derivative of f(x) = sin(x²) with respect to x is:",
            options = listOf("2x cos(x²)", "cos(x²)", "2 sin(x) cos(x)", "-2x cos(x²)"),
            correctOptionIndex = 0,
            explanation = "Using the Chain Rule: d/dx[sin(u)] = cos(u) · (du/dx). With u = x², du/dx = 2x. Thus f'(x) = 2x cos(x²).",
            topicTag = "Calculus"
        ),
        Question(
            id = 5,
            subjectId = Subject.CUET_SAMPLE_MOCK.id,
            sectionName = "Section II: Physics",
            questionText = "Electric potential at any point on equatorial line of a dipole is:",
            options = listOf("Zero", "Infinite", "Positive", "Negative"),
            correctOptionIndex = 0,
            explanation = "At any point on the equatorial line, the distance from +q is equal to the distance from -q (r₁ = r₂). Therefore, V = (1/4πε₀)(q/r - q/r) = 0.",
            topicTag = "Electrostatics"
        )
    )

    private val generalTestQuestions = listOf(
        Question(
            id = 1,
            subjectId = Subject.GENERAL_TEST.id,
            sectionName = "Section III: General Mental Ability",
            questionText = "Find the missing number in the following numerical series:\n4, 9, 25, 49, 121, ?",
            options = listOf("144", "169", "196", "225"),
            correctOptionIndex = 1,
            explanation = "The terms are squares of consecutive prime numbers: 2² = 4, 3² = 9, 5² = 25, 7² = 49, 11² = 121. The next prime number is 13, and 13² = 169.",
            topicTag = "Number Series"
        ),
        Question(
            id = 2,
            subjectId = Subject.GENERAL_TEST.id,
            sectionName = "Section III: Numerical Ability",
            questionText = "A trader marks goods 40% above the cost price and allows a discount of 25% on the marked price. What is the overall profit or loss percentage?",
            options = listOf("5% Profit", "10% Profit", "5% Loss", "15% Profit"),
            correctOptionIndex = 0,
            explanation = "Let Cost Price (CP) = 100.\nMarked Price (MP) = 100 + 40 = 140.\nDiscount = 25% of 140 = 35.\nSelling Price (SP) = 140 - 35 = 105.\nProfit = 105 - 100 = 5, which is 5% profit.",
            topicTag = "Profit & Loss"
        ),
        Question(
            id = 3,
            subjectId = Subject.GENERAL_TEST.id,
            sectionName = "Section III: Analytical Reasoning",
            questionText = "In a certain code language, if 'DELHI' is coded as 'CCIDD', how will 'BOMBAY' be coded in that same pattern?",
            options = listOf("AMJXVS", "ALJZVS", "AMJZWS", "BMKXVT"),
            correctOptionIndex = 0,
            explanation = "Pattern of letter shift: D(-1)=C, E(-2)=C, L(-3)=I, H(-4)=D, I(-5)=D.\nApplying decreasing shifts -1, -2, -3, -4, -5, -6 to BOMBAY:\nB(-1)=A, O(-2)=M, M(-3)=J, B(-4)=X, A(-5)=V, Y(-6)=S. Hence AMJXVS.",
            topicTag = "Coding-Decoding"
        ),
        Question(
            id = 4,
            subjectId = Subject.GENERAL_TEST.id,
            sectionName = "Section III: General Awareness",
            questionText = "Which constitutional body in India is empowered to recommend the distribution of tax revenues between the Union and the States?",
            options = listOf("Planning Commission", "NITI Aayog", "Finance Commission", "GST Council"),
            correctOptionIndex = 2,
            explanation = "Under Article 280 of the Constitution of India, the Finance Commission is constituted every 5 years by the President to make recommendations on financial devolution between the Centre and States.",
            topicTag = "Indian Polity"
        ),
        Question(
            id = 5,
            subjectId = Subject.GENERAL_TEST.id,
            sectionName = "Section III: Logical Deduction",
            questionText = "Statements:\nI. All apples are fruits.\nII. Some fruits are sweet.\nConclusions:\n1. Some apples are sweet.\n2. All fruits are apples.\nWhich conclusion logically follows?",
            options = listOf("Only Conclusion 1 follows", "Only Conclusion 2 follows", "Both follow", "Neither conclusion follows"),
            correctOptionIndex = 3,
            explanation = "From 'All apples are fruits' and 'Some fruits are sweet', the sweet fruits may not overlap with apples. Thus Conclusion 1 does not necessarily follow. Conclusion 2 is an unwarranted universal reversal. Neither follows.",
            topicTag = "Syllogism"
        ),
        Question(
            id = 6,
            subjectId = Subject.GENERAL_TEST.id,
            sectionName = "Section III: Quantitative Aptitude",
            questionText = "A train 180 meters long is running at a uniform speed of 54 km/h. How many seconds will it take to pass a stationary telegraph post completely?",
            options = listOf("10 seconds", "12 seconds", "15 seconds", "18 seconds"),
            correctOptionIndex = 1,
            explanation = "Speed in m/s = 54 × (5/18) = 15 m/s.\nTime taken = Distance / Speed = 180 m / 15 m/s = 12 seconds.",
            topicTag = "Time & Distance"
        ),
        Question(
            id = 7,
            subjectId = Subject.GENERAL_TEST.id,
            sectionName = "Section III: Logical Reasoning",
            questionText = "Pointing to a photograph, Rohan said, 'Her mother's only son is my father.' How is the lady in the photograph related to Rohan?",
            options = listOf("Mother", "Paternal Aunt (Bua)", "Grandmother", "Sister"),
            correctOptionIndex = 1,
            explanation = "'Her mother's only son' is the lady's brother. Rohan's father is that brother. Therefore, the lady is Rohan's father's sister, which is his Paternal Aunt (Bua).",
            topicTag = "Blood Relations"
        ),
        Question(
            id = 8,
            subjectId = Subject.GENERAL_TEST.id,
            sectionName = "Section III: Arithmetic Ability",
            questionText = "The ratio of ages of A and B is currently 4:5. Eight years hence, the ratio of their ages will become 5:6. What is the present age of A?",
            options = listOf("24 years", "28 years", "32 years", "40 years"),
            correctOptionIndex = 2,
            explanation = "Let present ages be 4x and 5x.\n(4x + 8) / (5x + 8) = 5/6\n6(4x + 8) = 5(5x + 8)\n24x + 48 = 25x + 40 => x = 8.\nPresent age of A = 4 × 8 = 32 years.",
            topicTag = "Ages & Ratios"
        ),
        Question(
            id = 9,
            subjectId = Subject.GENERAL_TEST.id,
            sectionName = "Section III: General Science / GK",
            questionText = "Which greenhouse gas has the highest global warming potential (GWP) per molecule among the following atmospheric gases?",
            options = listOf("Carbon Dioxide (CO₂)", "Methane (CH₄)", "Nitrous Oxide (N₂O)", "Sulfur Hexafluoride (SF₆)"),
            correctOptionIndex = 3,
            explanation = "Sulfur Hexafluoride (SF₆) has an extraordinarily high GWP of over 23,500 times that of CO₂ over a 100-year time horizon, making it the most potent greenhouse gas evaluated by the IPCC.",
            topicTag = "Environment & Ecology"
        ),
        Question(
            id = 10,
            subjectId = Subject.GENERAL_TEST.id,
            sectionName = "Section III: Direction Sense",
            questionText = "Ananya walks 15 km North, turns right and walks 20 km. She then turns right again and walks 15 km. Finally, she turns left and walks 10 km. How far is she now from her starting point?",
            options = listOf("20 km", "25 km", "30 km", "35 km"),
            correctOptionIndex = 2,
            explanation = "North 15 km and South 15 km cancel out on the vertical axis.\nHorizontal displacements: 20 km East + 10 km East = 30 km due East from starting position.",
            topicTag = "Direction & Distance"
        )
    )

    private val englishQuestions = listOf(
        Question(
            id = 11,
            subjectId = Subject.ENGLISH.id,
            sectionName = "Section IA: Reading Comprehension",
            passageTitle = ENGLISH_PASSAGE_TITLE,
            passageText = ENGLISH_PASSAGE_TEXT,
            questionText = "According to the passage, what is the primary warning raised by pedagogical scholars regarding AI in education?",
            options = listOf(
                "AI software is too expensive for developing nations to adopt",
                "AI cannot replicate essential human dimensions like empathy and moral mentorship",
                "AI algorithms make frequent arithmetic errors in student evaluations",
                "AI replaces teacher-student dialogue with automated exams"
            ),
            correctOptionIndex = 1,
            explanation = "The passage explicitly notes: 'The irreplaceable human dimension of schooling—empathy, mentorship, collaborative peer discourse, and moral guidance—cannot be replicated by algorithmic models.'",
            topicTag = "Passage Inference"
        ),
        Question(
            id = 12,
            subjectId = Subject.ENGLISH.id,
            sectionName = "Section IA: Reading Comprehension",
            passageTitle = ENGLISH_PASSAGE_TITLE,
            passageText = ENGLISH_PASSAGE_TEXT,
            questionText = "The word 'panacea' in the second paragraph most nearly means:",
            options = listOf("A temporary obstacle", "A universal remedy or cure-all", "A computational error", "A scientific hypothesis"),
            correctOptionIndex = 1,
            explanation = "'Panacea' originates from Greek meaning 'all-healing', denoting an alleged universal solution or remedy for all difficulties.",
            topicTag = "Vocabulary in Context"
        ),
        Question(
            id = 13,
            subjectId = Subject.ENGLISH.id,
            sectionName = "Section IA: Reading Comprehension",
            passageTitle = ENGLISH_PASSAGE_TITLE,
            passageText = ENGLISH_PASSAGE_TEXT,
            questionText = "What beneficial role does the author envisage for AI in relation to classroom educators?",
            options = listOf(
                "Replacing teachers so schools operate without staff",
                "Restricting students to pre-recorded video modules",
                "Relieving teachers of grading burdens so they can foster deeper critical thinking",
                "Grading only standardized multiple-choice entrance exams"
            ),
            correctOptionIndex = 2,
            explanation = "The final sentence states: 'Rather than replacing educators, AI functions most efficaciously as a teaching assistant, liberating teachers from grading routines so they can engage students in deeper inquiry, critical thinking...'",
            topicTag = "Passage Detail"
        ),
        Question(
            id = 14,
            subjectId = Subject.ENGLISH.id,
            sectionName = "Section IA: Vocabulary & Antonyms",
            questionText = "Choose the word that is opposite in meaning (Antonym) to: 'EPHEMERAL'",
            options = listOf("Transient", "Perpetual", "Fleeting", "Evanescent"),
            correctOptionIndex = 1,
            explanation = "'Ephemeral' means lasting for a very short time. Its antonym is 'Perpetual', meaning eternal or enduring forever.",
            topicTag = "Antonyms"
        ),
        Question(
            id = 15,
            subjectId = Subject.ENGLISH.id,
            sectionName = "Section IA: Idioms & Phrases",
            questionText = "Select the correct meaning of the idiom: 'To burn the candle at both ends'",
            options = listOf(
                "To waste electricity recklessly",
                "To work excessively hard from early morning until late night",
                "To provoke both sides in a debate",
                "To celebrate an occasion lavishly"
            ),
            correctOptionIndex = 1,
            explanation = "'To burn the candle at both ends' means to exhaust oneself by doing too much, particularly by working long hours without adequate rest.",
            topicTag = "Idioms"
        ),
        Question(
            id = 16,
            subjectId = Subject.ENGLISH.id,
            sectionName = "Section IA: Spotting Errors",
            questionText = "Identify the segment containing a grammatical error:\n[A] Neither the principal / [B] nor the teachers / [C] was present at / [D] the annual convocation.",
            options = listOf("[A]", "[B]", "[C]", "[D]"),
            correctOptionIndex = 2,
            explanation = "In correlative conjunctions 'Neither... nor...', the verb agrees with the closer subject. 'Teachers' is plural, so it must take the plural verb 'were present at' instead of 'was present at'. Hence segment [C] has the error.",
            topicTag = "Subject-Verb Agreement"
        ),
        Question(
            id = 17,
            subjectId = Subject.ENGLISH.id,
            sectionName = "Section IA: Sentence Completion",
            questionText = "The diplomat managed to negotiate the peace accord with consummate _______, calming tensions on both sides.",
            options = listOf("Taciturnity", "Finesse", "Insolence", "Apathy"),
            correctOptionIndex = 1,
            explanation = "'Finesse' means intricate and refined skill, delicacy, or tact in handling difficult situations.",
            topicTag = "Verbal Vocabulary"
        ),
        Question(
            id = 18,
            subjectId = Subject.ENGLISH.id,
            sectionName = "Section IA: Para-Jumbles",
            questionText = "Rearrange the sentences into a coherent paragraph:\nP: Space exploration inspires new technological breakthroughs.\nQ: These innovations subsequently benefit everyday healthcare and communication.\nR: However, space missions require substantial public and private funding.\nS: Balancing these fiscal commitments remains a recurring debate.",
            options = listOf("P - Q - R - S", "Q - P - S - R", "R - S - P - Q", "P - R - Q - S"),
            correctOptionIndex = 0,
            explanation = "P establishes the overarching premise (technological inspiration), Q elaborates on practical applications (healthcare/communication), R introduces the financial challenge, and S concludes with policy balancing.",
            topicTag = "Para-Jumbles"
        ),
        Question(
            id = 19,
            subjectId = Subject.ENGLISH.id,
            sectionName = "Section IA: One Word Substitution",
            questionText = "A person who is indifferent to both pleasure and pain is called a/an:",
            options = listOf("Stoic", "Hedonist", "Altruist", "Iconoclast"),
            correctOptionIndex = 0,
            explanation = "A 'Stoic' is an individual who can endure pain or adversity without displaying feelings and without complaining.",
            topicTag = "One Word Substitution"
        ),
        Question(
            id = 20,
            subjectId = Subject.ENGLISH.id,
            sectionName = "Section IA: Synonyms",
            questionText = "Select the most appropriate synonym for: 'CANDID'",
            options = listOf("Deceptive", "Frank", "Ambiguous", "Reticent"),
            correctOptionIndex = 1,
            explanation = "'Candid' means truthful, straightforward, and sincere. 'Frank' is its exact synonym.",
            topicTag = "Synonyms"
        )
    )

    private val mathQuestions = listOf(
        Question(
            id = 21,
            subjectId = Subject.MATHEMATICS.id,
            sectionName = "Section II: Mathematics",
            questionText = "Evaluate the definite integral:\n∫ from 0 to π/2 of (sin x) / (sin x + cos x) dx",
            options = listOf("π/2", "π/4", "π/8", "1"),
            correctOptionIndex = 1,
            explanation = "Let I = ∫₀^(π/2) sin x / (sin x + cos x) dx.\nUsing King's property ∫₀^a f(x)dx = ∫₀^a f(a - x)dx:\nI = ∫₀^(π/2) cos x / (cos x + sin x) dx.\nAdding both equations: 2I = ∫₀^(π/2) 1 dx = π/2.\nTherefore, I = π/4.",
            topicTag = "Definite Integrals"
        ),
        Question(
            id = 22,
            subjectId = Subject.MATHEMATICS.id,
            sectionName = "Section II: Mathematics",
            questionText = "If matrix A is an invertible square matrix of order 3 and |A| = 4, then find |adj(A)|.",
            options = listOf("4", "8", "16", "64"),
            correctOptionIndex = 2,
            explanation = "For an n × n matrix, |adj(A)| = |A|^(n - 1).\nHere n = 3, so |adj(A)| = |A|^(3 - 1) = |A|² = 4² = 16.",
            topicTag = "Matrices & Determinants"
        ),
        Question(
            id = 23,
            subjectId = Subject.MATHEMATICS.id,
            sectionName = "Section II: Mathematics",
            questionText = "Two fair dice are thrown simultaneously. What is the conditional probability that the sum of numbers is 8 given that the first die shows an even number?",
            options = listOf("1/6", "5/36", "3/18", "1/18"),
            correctOptionIndex = 0,
            explanation = "Sample space where first die is even: {2, 4, 6} × {1..6} = 18 outcomes.\nOutcomes yielding sum 8: (2,6), (4,4), (6,2) = 3 outcomes.\nProbability = 3 / 18 = 1/6.",
            topicTag = "Probability"
        ),
        Question(
            id = 24,
            subjectId = Subject.MATHEMATICS.id,
            sectionName = "Section II: Mathematics",
            questionText = "Find the order and degree respectively of the differential equation:\n[1 + (dy/dx)²]^(3/2) = d²y/dx²",
            options = listOf("Order = 1, Degree = 3", "Order = 2, Degree = 2", "Order = 2, Degree = 1", "Order = 3, Degree = 2"),
            correctOptionIndex = 1,
            explanation = "Squaring both sides to eliminate the fractional exponent: [1 + (dy/dx)²]³ = (d²y/dx²)².\nThe highest order derivative is d²y/dx² (Order 2), and its power is 2 (Degree 2).",
            topicTag = "Differential Equations"
        ),
        Question(
            id = 25,
            subjectId = Subject.MATHEMATICS.id,
            sectionName = "Section II: Mathematics",
            questionText = "What is the projection of the vector a = 2i + 3j + 2k onto the vector b = i + 2j + k?",
            options = listOf("10 / √6", "5 / √6", "10 / 6", "12 / √14"),
            correctOptionIndex = 0,
            explanation = "Projection = (a · b) / |b|.\na · b = (2)(1) + (3)(2) + (2)(1) = 2 + 6 + 2 = 10.\n|b| = √(1² + 2² + 1²) = √6.\nProjection = 10 / √6.",
            topicTag = "Vector Algebra"
        ),
        Question(
            id = 26,
            subjectId = Subject.MATHEMATICS.id,
            sectionName = "Section II: Mathematics",
            questionText = "The function f(x) = x³ - 3x² + 3x - 10 is:",
            options = listOf("Strictly increasing on R", "Strictly decreasing on R", "Increasing in (0,1) and decreasing in (1,2)", "Neither increasing nor decreasing"),
            correctOptionIndex = 0,
            explanation = "f'(x) = 3x² - 6x + 3 = 3(x - 1)² ≥ 0 for all x ∈ R.\nSince f'(x) > 0 except at isolated point x = 1, f(x) is strictly increasing everywhere on R.",
            topicTag = "Application of Derivatives"
        ),
        Question(
            id = 27,
            subjectId = Subject.MATHEMATICS.id,
            sectionName = "Section II: Mathematics",
            questionText = "Find the angle between the lines whose direction ratios are proportional to <1, 1, 0> and <1, 0, 1>.",
            options = listOf("30°", "45°", "60°", "90°"),
            correctOptionIndex = 2,
            explanation = "cos θ = |(a₁a₂ + b₁b₂ + c₁c₂)| / (√(a₁² + b₁² + c₁²) √(a₂² + b₂² + c₂²))\ncos θ = |(1·1 + 1·0 + 0·1)| / (√(1+1+0) √(1+0+1)) = 1 / (√2 · √2) = 1/2.\nθ = 60° (or π/3 radians).",
            topicTag = "3D Geometry"
        ),
        Question(
            id = 28,
            subjectId = Subject.MATHEMATICS.id,
            sectionName = "Section II: Mathematics",
            questionText = "What is the domain of the function f(x) = sin⁻¹(2x - 3)?",
            options = listOf("[1, 2]", "[0, 1]", "[-1, 1]", "[2, 3]"),
            correctOptionIndex = 0,
            explanation = "For sin⁻¹(u), -1 ≤ u ≤ 1.\n-1 ≤ 2x - 3 ≤ 1\nAdd 3: 2 ≤ 2x ≤ 4\nDivide by 2: 1 ≤ x ≤ 2.\nDomain is [1, 2].",
            topicTag = "Inverse Trigonometry"
        ),
        Question(
            id = 29,
            subjectId = Subject.MATHEMATICS.id,
            sectionName = "Section II: Mathematics",
            questionText = "If a relation R on the set A = {1, 2, 3} is given by R = {(1,1), (2,2), (3,3), (1,2), (2,1)}, what is the nature of R?",
            options = listOf("Equivalence relation", "Reflexive and transitive only", "Reflexive and symmetric only", "Symmetric only"),
            correctOptionIndex = 0,
            explanation = "Reflexive: contains (1,1), (2,2), (3,3).\nSymmetric: (1,2) ∈ R and (2,1) ∈ R.\nTransitive: (1,2) and (2,1) gives (1,1) ∈ R; (2,1) and (1,2) gives (2,2) ∈ R.\nThus R is an equivalence relation.",
            topicTag = "Relations & Functions"
        ),
        Question(
            id = 30,
            subjectId = Subject.MATHEMATICS.id,
            sectionName = "Section II: Mathematics",
            questionText = "In a Linear Programming Problem (LPP), where does the optimal value of the objective function always occur?",
            options = listOf("At the center of the feasible region", "At one of the corner points (vertices) of the feasible region", "Anywhere on the boundary lines", "Outside the feasible polygon"),
            correctOptionIndex = 1,
            explanation = "By the Fundamental Theorem of Linear Programming, if a feasible region is bounded, the optimum (maximum or minimum) value of the linear objective function occurs at a corner point (vertex) of the feasible region.",
            topicTag = "Linear Programming"
        )
    )

    private val physicsQuestions = listOf(
        Question(
            id = 31,
            subjectId = Subject.PHYSICS.id,
            sectionName = "Section II: Physics",
            questionText = "Two identical point charges Q are placed at distance 2r apart. A third charge -q is placed at the exact midpoint. For the entire system to be in equilibrium, what must be the value of q?",
            options = listOf("Q/2", "Q/4", "Q/8", "-Q/4"),
            correctOptionIndex = 1,
            explanation = "Equilibrium of one outer charge Q: Coulomb force from central charge (-q) at distance r must balance repulsive force from other charge Q at distance 2r:\nk·Q·q / r² = k·Q² / (2r)² => q / r² = Q / 4r² => q = Q/4.",
            topicTag = "Electrostatics"
        ),
        Question(
            id = 32,
            subjectId = Subject.PHYSICS.id,
            sectionName = "Section II: Physics",
            questionText = "A parallel plate capacitor is charged by a battery and then disconnected. A dielectric slab with dielectric constant K > 1 is inserted between the plates. What happens to the stored energy?",
            options = listOf("Increases by factor K", "Decreases by factor K", "Remains unchanged", "Increases by factor K²"),
            correctOptionIndex = 1,
            explanation = "When disconnected, charge Q remains constant. Capacitance increases: C' = K·C. Stored energy U = Q² / (2C). Therefore U' = Q² / (2KC) = U / K, so stored energy decreases by a factor of K.",
            topicTag = "Capacitance"
        ),
        Question(
            id = 33,
            subjectId = Subject.PHYSICS.id,
            sectionName = "Section II: Physics",
            questionText = "Lenz's Law in electromagnetic induction is a direct consequence of the conservation of which fundamental physical quantity?",
            options = listOf("Electric Charge", "Linear Momentum", "Energy", "Magnetic Flux"),
            correctOptionIndex = 2,
            explanation = "Lenz's Law ensures that mechanical work done against opposing induced magnetic forces is converted into electrical energy, satisfying the Law of Conservation of Energy.",
            topicTag = "Electromagnetic Induction"
        ),
        Question(
            id = 34,
            subjectId = Subject.PHYSICS.id,
            sectionName = "Section II: Physics",
            questionText = "In Young's Double Slit Experiment, if the entire apparatus is immersed in water (refractive index μ = 4/3), what happens to the fringe width β?",
            options = listOf("Increases by 4/3", "Decreases to 3/4 of original value", "Remains unaffected", "Doubles"),
            correctOptionIndex = 1,
            explanation = "Fringe width β = λD/d. In a medium of refractive index μ, wavelength λ' = λ/μ. Hence β' = β/μ = β / (4/3) = (3/4)β, so it decreases to 3/4.",
            topicTag = "Wave Optics"
        ),
        Question(
            id = 35,
            subjectId = Subject.PHYSICS.id,
            sectionName = "Section II: Physics",
            questionText = "If the momentum of a subatomic particle is doubled, how does its de Broglie wavelength change?",
            options = listOf("Doubles", "Halves", "Quadruples", "Remains unchanged"),
            correctOptionIndex = 1,
            explanation = "de Broglie wavelength λ = h / p. When momentum p is doubled to 2p, λ' = h / (2p) = λ/2, so the wavelength is halved.",
            topicTag = "Dual Nature of Matter"
        ),
        Question(
            id = 36,
            subjectId = Subject.PHYSICS.id,
            sectionName = "Section II: Physics",
            questionText = "According to Bohr's model of the hydrogen atom, the radius of the nth orbit is directly proportional to:",
            options = listOf("n", "n²", "1/n", "1/n²"),
            correctOptionIndex = 1,
            explanation = "In Bohr's theory, the orbital radius is given by rₙ = (ε₀ h² / π m e²) · (n² / Z) ≈ 0.529 · n² Å for Hydrogen (Z=1). Thus rₙ ∝ n².",
            topicTag = "Atoms & Nuclei"
        ),
        Question(
            id = 37,
            subjectId = Subject.PHYSICS.id,
            sectionName = "Section II: Physics",
            questionText = "Which logic gate outputs a LOW (0) signal if and only if both its inputs are HIGH (1)?",
            options = listOf("NOR Gate", "NAND Gate", "XOR Gate", "AND Gate"),
            correctOptionIndex = 1,
            explanation = "A NAND gate produces a 0 (false) only when both inputs are 1 (true). For all other input combinations (00, 01, 10), its output is 1.",
            topicTag = "Semiconductor Electronics"
        ),
        Question(
            id = 38,
            subjectId = Subject.PHYSICS.id,
            sectionName = "Section II: Physics",
            questionText = "An alternating current circuit contains a pure inductor L connected to voltage V = V₀ sin(ωt). The current in the circuit leads or lags the voltage by:",
            options = listOf("Leads voltage by π/2", "Lags voltage by π/2", "In phase with voltage", "Lags voltage by π"),
            correctOptionIndex = 1,
            explanation = "In a purely inductive circuit, current lags behind voltage by a phase angle of 90° (π/2 radians), i.e., i = i₀ sin(ωt - π/2).",
            topicTag = "Alternating Current"
        ),
        Question(
            id = 39,
            subjectId = Subject.PHYSICS.id,
            sectionName = "Section II: Physics",
            questionText = "What is the angle of dip at the Earth's magnetic equator?",
            options = listOf("0°", "45°", "90°", "180°"),
            correctOptionIndex = 0,
            explanation = "At the magnetic equator, the Earth's magnetic field lines are completely horizontal (parallel to the surface). Therefore, the vertical component is zero and the angle of dip is 0°.",
            topicTag = "Magnetism & Matter"
        ),
        Question(
            id = 40,
            subjectId = Subject.PHYSICS.id,
            sectionName = "Section II: Physics",
            questionText = "A galvanometer of resistance 50 Ω gives full-scale deflection for 2 mA. What shunt resistance is needed to convert it into an ammeter of range 0 to 5 A?",
            options = listOf("0.02 Ω", "0.20 Ω", "2.00 Ω", "0.002 Ω"),
            correctOptionIndex = 0,
            explanation = "Shunt S = (I_g · G) / (I - I_g) ≈ (2 × 10⁻³ × 50) / 5 = 0.10 / 5 = 0.02 Ω.",
            topicTag = "Moving Charges & Magnetism"
        )
    )

    private val economicsQuestions = listOf(
        Question(
            id = 41,
            subjectId = Subject.ECONOMICS.id,
            sectionName = "Section II: Economics",
            questionText = "If a 10% decrease in the price of a good results in a 25% increase in its quantity demanded, the price elasticity of demand is:",
            options = listOf("0.4 (Inelastic)", "2.5 (Elastic)", "1.0 (Unitary)", "25.0 (Perfect)"),
            correctOptionIndex = 1,
            explanation = "Price Elasticity of Demand (Ed) = (% Change in Qty Demanded) / (% Change in Price) = 25% / 10% = 2.5. Since Ed > 1, demand is price elastic.",
            topicTag = "Microeconomics"
        ),
        Question(
            id = 42,
            subjectId = Subject.ECONOMICS.id,
            sectionName = "Section II: Economics",
            questionText = "Which of the following items is EXCLUDED from the computation of Gross Domestic Product (GDP)?",
            options = listOf(
                "Export earnings of domestic IT firms",
                "Sale of a second-hand automobile",
                "Government expenditure on infrastructure",
                "Salaries paid to school teachers"
            ),
            correctOptionIndex = 1,
            explanation = "Transactions involving second-hand goods do not reflect current production of new output; they merely represent transfer of ownership of previously produced assets, so they are excluded from GDP.",
            topicTag = "National Income"
        ),
        Question(
            id = 43,
            subjectId = Subject.ECONOMICS.id,
            sectionName = "Section II: Economics",
            questionText = "When the Reserve Bank of India increases the Cash Reserve Ratio (CRR), what direct effect does it have on the banking system's credit creation capacity?",
            options = listOf(
                "Commercial banks have more surplus funds to lend",
                "Commercial banks have fewer lendable funds, curbing money supply",
                "Interest rates automatically collapse to zero",
                "Foreign exchange reserves immediately double"
            ),
            correctOptionIndex = 1,
            explanation = "A higher CRR mandates that commercial banks hold a greater fraction of net demand and time liabilities with the RBI, reducing their excess reserves and contractionary impact on credit creation.",
            topicTag = "Money & Banking"
        ),
        Question(
            id = 44,
            subjectId = Subject.ECONOMICS.id,
            sectionName = "Section II: Economics",
            questionText = "The Marginal Propensity to Consume (MPC) is 0.8. What is the value of the investment multiplier (k)?",
            options = listOf("2", "4", "5", "8"),
            correctOptionIndex = 2,
            explanation = "Investment Multiplier k = 1 / (1 - MPC) = 1 / (1 - 0.8) = 1 / 0.2 = 5.",
            topicTag = "Determination of Income"
        ),
        Question(
            id = 45,
            subjectId = Subject.ECONOMICS.id,
            sectionName = "Section II: Economics",
            questionText = "Which institution replaced the Planning Commission of India on January 1, 2015?",
            options = listOf("Finance Commission", "NITI Aayog", "National Development Council", "Inter-State Council"),
            correctOptionIndex = 1,
            explanation = "The National Institution for Transforming India (NITI Aayog) was constituted on January 1, 2015, replacing the 65-year-old Planning Commission to foster cooperative federalism.",
            topicTag = "Indian Economic Development"
        ),
        Question(
            id = 46,
            subjectId = Subject.ECONOMICS.id,
            sectionName = "Section II: Economics",
            questionText = "In the Balance of Payments (BoP), where are unilateral transfers (such as remittances sent by citizens working overseas) categorized?",
            options = listOf("Current Account - Invisibles", "Capital Account - FDI", "Official Reserve Transactions", "Errors and Omissions"),
            correctOptionIndex = 0,
            explanation = "Unilateral transfers (gifts, remittances, grants) are one-way flows that do not create future claims or liabilities and are recorded under the Invisibles section of the Current Account.",
            topicTag = "Balance of Payments"
        ),
        Question(
            id = 47,
            subjectId = Subject.ECONOMICS.id,
            sectionName = "Section II: Economics",
            questionText = "An Indifference Curve is convex to the origin due to the operation of which economic principle?",
            options = listOf(
                "Diminishing Marginal Rate of Substitution (MRS)",
                "Law of Diminishing Marginal Utility",
                "Constant Returns to Scale",
                "Increasing Opportunity Costs"
            ),
            correctOptionIndex = 0,
            explanation = "As a consumer consumes more units of one good, the willingness to give up units of the other good diminishes, leading to Diminishing MRS and ensuring convexity to the origin.",
            topicTag = "Consumer Behavior"
        ),
        Question(
            id = 48,
            subjectId = Subject.ECONOMICS.id,
            sectionName = "Section II: Economics",
            questionText = "What is the primary indicator of economic health that subtracts depreciation (consumption of fixed capital) from Gross National Product (GNP)?",
            options = listOf("GDP at Factor Cost", "Net National Product (NNP)", "Personal Disposable Income", "Gross Value Added (GVA)"),
            correctOptionIndex = 1,
            explanation = "NNP = GNP - Depreciation (Consumption of Fixed Capital). NNP at factor cost is formally known as National Income.",
            topicTag = "National Accounting"
        ),
        Question(
            id = 49,
            subjectId = Subject.ECONOMICS.id,
            sectionName = "Section II: Economics",
            questionText = "Which tax among the following is a progressive and direct tax in India?",
            options = listOf("Goods and Services Tax (GST)", "Customs Duty", "Personal Income Tax", "Central Excise Duty"),
            correctOptionIndex = 2,
            explanation = "Personal Income Tax is paid directly by the individual on whom it is legally levied, and tax slab rates increase as taxable income rises (progressive tax structure).",
            topicTag = "Government Budget"
        ),
        Question(
            id = 50,
            subjectId = Subject.ECONOMICS.id,
            sectionName = "Section II: Economics",
            questionText = "The Green Revolution introduced high-yielding varieties (HYV) of seeds predominantly for which major food grains initially?",
            options = listOf("Rice and Wheat", "Millets and Pulses", "Cotton and Jute", "Barley and Maize"),
            correctOptionIndex = 0,
            explanation = "Initiated in the late 1960s with dwarf Mexican varieties, the Green Revolution focused primarily on wheat in northwestern India and later expanded to rice production.",
            topicTag = "Indian Agriculture"
        )
    )
}
