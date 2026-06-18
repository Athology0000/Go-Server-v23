package crypto

import "testing"

// DummyVerifyPassword must be callable (its internal hash is initialized at package init) so
// authentication paths can spend equivalent argon2 work on the account-not-found branch.
func TestDummyVerifyPassword(t *testing.T) {
	if dummyVerifyHash == "" {
		t.Fatal("dummyVerifyHash was not initialized at package init")
	}
	// The internal hash must be a real, verifiable argon2id hash (so the dummy path does the same
	// work as a genuine verify): the matching input verifies, a wrong input does not.
	if ok, err := VerifyPassword("phantom/dummy-verify/target", dummyVerifyHash); err != nil || !ok {
		t.Fatalf("dummy hash should verify against its own input: ok=%v err=%v", ok, err)
	}
	if ok, _ := VerifyPassword("not-the-target", dummyVerifyHash); ok {
		t.Error("dummy hash must not verify against an arbitrary password")
	}
	// Must not panic.
	DummyVerifyPassword()
}
