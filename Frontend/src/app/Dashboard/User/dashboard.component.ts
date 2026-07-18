import {Component, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {Router} from '@angular/router';
import {ToastrService} from 'ngx-toastr';
import {LoggedInUser} from '../../Model/loggedUser.model';
import {Subscription} from 'rxjs';
import {AuthService} from '../../Service/Auth/auth.service';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import {UserService} from '../../Service/user.service';
import {HttpHeaders} from '@angular/common/http';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  standalone: false,
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit, OnDestroy {

  constructor(
    private router: Router,
    private toastr: ToastrService,
    private authService: AuthService,
    private fb: FormBuilder,
    private userService: UserService
  ) {
    this.initTicketForm();
  }

  currentUser: LoggedInUser | null = null;
  userSubscription: Subscription | undefined;
  role: string = "";
  ticketForm!: FormGroup;
  selectedFile: File | null = null;
  isDragging = false;
  @ViewChild('fileInput') fileInputVariable: any;

  // 1. Nuovo metodo privato riutilizzabile per la validazione
  private isFileExtensionAllowed(file: File): boolean {
    const allowedExtensions = ['txt', 'json', 'log', 'csv'];
    const fileExtension = file.name.split('.').pop()?.toLowerCase();
    return !!fileExtension && allowedExtensions.includes(fileExtension);
  }

  // 2. Il metodo di selezione da click diventa leggerissimo
  onFileSelected(event: any): void {
    const file: File = event.target.files[0];
    if (file) {
      if (this.isFileExtensionAllowed(file)) {
        this.selectedFile = file;
        this.toastr.success("File caricato: " + file.name);
      } else {
        this.selectedFile = null;
        event.target.value = '';
        this.toastr.error("Formato non supportato! Usa solo .txt, .json, .log o .csv");
      }
    }
  }

  // 3. Il metodo del drag & drop usa la stessa identica logica
  onDrop(event: DragEvent) {
    event.preventDefault();
    this.isDragging = false;
    if (event.dataTransfer?.files.length) {
      const file = event.dataTransfer.files[0];

      if (this.isFileExtensionAllowed(file)) {
        this.selectedFile = file;
        this.toastr.success("File caricato tramite drag & drop: " + file.name);
      } else {
        this.selectedFile = null;
        this.toastr.error("Formato non supportato! Usa solo .txt, .json, .log o .csv");
      }
    }
  }

  initTicketForm(): void {
    this.ticketForm = this.fb.group({
      problem_title: ['', [Validators.required]],
      description: ['', [Validators.required]],
      problem: [''],
      urgency_percepite: ['', Validators.required]
    });
  }

  onDragOver(event: DragEvent) {
    event.preventDefault();
    this.isDragging = true;
  }

  onDragLeave() {
    this.isDragging = false;
  }

  removeFile(): void {
    this.selectedFile = null;
    if (this.fileInputVariable) {
      this.fileInputVariable.nativeElement.value = '';
    }
    // Opzionale: resetta lo stato del form per togliere i bordi rossi se avevi iniziato a scrivere
    if (!this.ticketForm.dirty) {
      this.ticketForm.markAsPristine();
      this.ticketForm.markAsUntouched();
    }
    this.toastr.info("Allegato rimosso");
  }

  canSubmit(): boolean {
    const formValues = this.ticketForm.value;
    const isFormEmpty = !formValues.problem_title?.trim() &&
      !formValues.description?.trim() &&
      !formValues.urgency_percepite;
    const isFormValid = this.ticketForm.valid;
    return (this.selectedFile !== null && isFormEmpty) || isFormValid;
  }

  onSubmitTicket() {
    const hasFormContent = this.ticketForm.value
    if (hasFormContent || this.selectedFile || (this.selectedFile && !this.ticketForm.value.problem_title)) {
      this.userService.createTicket(this.ticketForm.value, this.selectedFile).subscribe({
        next: (res) => this.toastr.info(res),
        error: (err) => this.toastr.error(err.error)
      });
    }
  }

  ngOnInit(): void {
    this.userSubscription = this.authService.currentUser$.subscribe(user => {
      this.currentUser = user;
      if (this.currentUser) {
        this.role = this.currentUser.role;
      } else {
        this.router.navigate(['/login']);
      }
    });
  }

  onLogout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }

  ngOnDestroy(): void {
    this.userSubscription?.unsubscribe();
  }

}
