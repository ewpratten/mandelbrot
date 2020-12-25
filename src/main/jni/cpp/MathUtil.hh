#if !defined(_MATHUTIL_HH)
#define _MATHUTIL_HH

#define map(val, inLow, inHigh, outLow, outHigh) \
    (val - inLow) * (outHigh - outLow) / (inHigh - inLow) + outLow

#endif  // _MATHUTIL_HH
