# Voice test fixture

`en.zip` contains 34 synthesized English number-word MP3 samples used for the
voice-template upload API. It is test data, separate from executable assertions.
No credentials or user recordings are included.

Override with `ENGAGELAB_VOICE_FILE` to provide another valid complete MP3 ZIP.
The native suite checks the archive before upload, compares the complete file
bytes with the observed multipart request, and preserves existing templates.

SHA-256: `922576bb34fd7bd8813621db73cb73d942d018e85df8b70ba2e5fb64e4975cf6`
