import { ChangeDetectorRef, Component, OnDestroy, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { ToastrService } from 'ngx-toastr';
import { LoggedInUser } from '../../Model/loggedUser.model';
import { Subscription, from, concatMap, finalize, tap, switchMap } from 'rxjs';
import { AuthService } from '../../Service/Auth/auth.service';
import { TicketAdminService } from '../../Service/admin.service';
import { TicketAdminDTO } from '../../Model/adminTicket.model';
import { ALL_STATES, State, Category } from '../../Model/ticketModel.model';
import { HttpHeaders } from '@angular/common/http';
import {noteDTO, noteViewDTO} from '../../Model/note.model';
import {NoteService} from '../../Service/note.service';

@Component({
  selector: 'app-dashboardAdmin',
  templateUrl: './dashboardAdmin.component.html',
  styleUrls: ['./dashboardAdmin.component.scss'],
  standalone: false
})
export class DashboardAdminComponent implements OnInit, OnDestroy {
  currentUser: LoggedInUser | null = null;
  userSubscription: Subscription | undefined;
  role: string = "";

  tickets: TicketAdminDTO[] = [];
  selectedRows = new Set<number>();
  isLoading = false;

  showFilterMenu = false;
  filters: any = {
    category: '',
    keyword: '',
    priority: null,
    state: '',
    startDate: ''
  };

  readonly allStates = ALL_STATES;
  readonly allCategories: Category[] = ['network', 'database', 'bug', 'configuration', 'other'];
  readonly priorities = [1, 2, 3, 4, 5];

  currentView: 'tickets' | 'blackboard' = 'tickets';
  notes: noteViewDTO[] = [];
  notesPage = 0;
  notesPageSize = 5;
  notesTotalPages = 0;
  isNotesLoading = false;

  newNote: noteDTO = {
    title: '',
    content: '',
    date: '',
    id_Ticket: 0,
    ticketTitle: ''
  };

  editingNoteId: number | null = null;
  editingNoteContent: string = '';

  constructor(
    private router: Router,
    private toastr: ToastrService,
    private authService: AuthService,
    private ticketAdminService: TicketAdminService,
    private noteService: NoteService,
    private cdr: ChangeDetectorRef
  ) {}

  stats: any = null;

  showNoteDeleteConfirmModal = false;
  noteIdToDelete: number | null = null;

  triggerNoteDeleteConfirm(noteId: number): void {
    this.noteIdToDelete = noteId;
    this.showNoteDeleteConfirmModal = true;
    this.cdr.detectChanges();
  }

  cancelNoteDelete(): void {
    this.showNoteDeleteConfirmModal = false;
    this.noteIdToDelete = null;
    this.cdr.detectChanges();
  }

  confirmNoteDelete(): void {
    if (this.noteIdToDelete === null) return;

    this.isNotesLoading = true;
    this.showNoteDeleteConfirmModal = false; // Chiude il modale immediatamente
    this.cdr.detectChanges();

    this.noteService.deleteNote(this.noteIdToDelete).subscribe({
      next: (msg) => {
        this.toastr.success(msg);

        if (this.notes.length === 1 && this.notesPage > 0) {
          this.notesPage--;
        }

        this.noteIdToDelete = null;
        this.loadNotes();
      },
      error: () => {
        this.toastr.error('Eliminazione nota fallita');
        this.isNotesLoading = false;
        this.noteIdToDelete = null;
        this.cdr.detectChanges();
      }
    });
  }

  loadStats(): void {
    this.ticketAdminService.getGlobalStats().subscribe({
      next: (data) => {
        this.stats = data;
        this.cdr.detectChanges();
      },
      error: () => this.toastr.error('Impossibile aggiornare il report globale')
    });
  }

  ngOnInit(): void {
    this.userSubscription = this.authService.currentUser$.subscribe(user => {
      if (user) {
        this.currentUser = user;
        this.role = user.role;
        this.loadTickets();
        this.loadStats();
        this.loadNotes();
      } else {
        this.router.navigate(['/login']);
      }
    });
  }

  switchView(view: 'tickets' | 'blackboard'): void {
    this.currentView = view;
    if (view === 'blackboard') {
      this.loadNotes();
    }
    this.cdr.detectChanges();
  }

  loadNotes(): void {
    this.isNotesLoading = true;
    this.noteService.viewNotes(this.notesPage, this.notesPageSize).subscribe({
      next: (response: any) => {
        this.notes = response.content;
        this.notesTotalPages = response.totalPages;
        this.isNotesLoading = false;
        this.cdr.detectChanges();
      },
      error: () => {
        this.toastr.error('Errore nel caricamento delle note');
        this.isNotesLoading = false;
        this.cdr.detectChanges();
      }
    });
  }

  onSaveNote(): void {
    if (!this.newNote.title.trim() || !this.newNote.content.trim() || this.newNote.id_Ticket <= 0) {
      this.toastr.warning('Compila tutti i campi obbligatori e seleziona un Ticket!');
      return;
    }

    this.newNote.date = new Date().toISOString();

    this.noteService.createNote(this.newNote).subscribe({
      next: (msg) => {
        this.toastr.success(msg);
        this.resetNoteForm();
        this.loadNotes();
      },
      error: (err) => this.toastr.error(err.error || 'Impossibile creare la nota')
    });
  }

  enableNoteEdit(note: noteViewDTO): void {
    this.editingNoteId = note.id;
    this.editingNoteContent = note.content;
  }

  cancelNoteEdit(): void {
    this.editingNoteId = null;
    this.editingNoteContent = '';
  }

  onUpdateNote(noteId: number): void {
    if (!this.editingNoteContent.trim()) {
      this.toastr.warning('Il contenuto della nota non può essere vuoto!');
      return;
    }

    this.noteService.updateNote(noteId, this.editingNoteContent).subscribe({
      next: (msg) => {
        this.toastr.success(msg);
        this.cancelNoteEdit();
        this.loadNotes();
      },
      error: () => this.toastr.error('Aggiornamento nota fallito')
    });
  }

  resetNoteForm(): void {
    this.newNote = {
      title: '',
      content: '',
      date: '',
      id_Ticket: 0,
      ticketTitle: ''
    };
  }

  onStateChange(ticket: TicketAdminDTO, event: any): void {
    const newState = event.target.value as State;
    const oldState = ticket.state;
    ticket.state = newState;

    this.ticketAdminService.updateState(ticket.id, newState).subscribe({
      next: (res: string) => {
        this.toastr.success(res);
        this.loadTickets();
        this.loadStats();
      },
      error: (err : string) => {
        this.toastr.error(err);
        ticket.state = oldState;
      }
    });
  }

  confirmDelete(): void {
    this.showDeleteConfirmModal = false;
    this.isLoading = true;
    this.cdr.detectChanges();

    const ticketsToDelete = this.tickets.filter(t => this.selectedRows.has(t.id));

    from(ticketsToDelete).pipe(
      concatMap((ticket) => this.ticketAdminService.deleteTicket(ticket.id)),
      finalize(() => {
        this.selectedRows.clear();
        this.isLoading = false;

        if (this.tickets.length === ticketsToDelete.length && this.currentPage > 0) {
          this.currentPage--;
        }

        if (this.hasActiveFilters()) {
          this.onSearch(false);
        } else {
          this.loadTickets();
        }

        this.loadStats();
        this.cdr.detectChanges();
      })
    ).subscribe({
      error: (err) => {
        this.toastr.error(err.error || 'Errore durante l\'eliminazione');
        this.isLoading = false;
      }
    });
  }

  getKeywordList(keywords: string | null): string[] {
    if (!keywords) return [];
    return keywords.split(/[,\s]+/).filter(k => k && k.trim().length > 0);
  }

  currentPage: number = 0;
  pageSize: number = 10;
  totalElements: number = 0;
  totalPages: number = 0;

  get pageNumbersArray(): number[] {
    return Array.from({ length: this.totalPages }, (_, i) => i);
  }

  getPriorityClass(level: number): string {
    if (level >= 4) return 'prio-high';
    if (level >= 2) return 'prio-med';
    return 'prio-low';
  }

  selectedTicketForModal: TicketAdminDTO | null = null;
  showInfoModal = false;

  openInfoModal(ticket: TicketAdminDTO): void {
    this.selectedTicketForModal = ticket;
    this.showInfoModal = true;
    this.cdr.detectChanges();
  }

  closeInfoModal(): void {
    this.showInfoModal = false;
    this.selectedTicketForModal = null;
    this.cdr.detectChanges();
  }

  hasActiveFilters(): boolean {
    return !!(this.filters.category || this.filters.keyword || this.filters.priority || this.filters.state || this.filters.startDate);
  }

  loadTickets(): void {
    this.isLoading = true;
    this.cdr.detectChanges();

    this.ticketAdminService.viewTickets(this.currentPage, this.pageSize).subscribe({
      next: (response: any) => {
        this.tickets = [...response.content];
        this.totalElements = response.totalElements;
        this.totalPages = response.totalPages;
        this.isLoading = false;
        this.cdr.detectChanges();
      },
      error: (err: any) => {
        this.toastr.error(err.error || 'Errore nel caricamento dei ticket');
        this.isLoading = false;
        this.cdr.detectChanges();
      }
    });
  }

  onSearch(resetPage: boolean = false): void {
    if (resetPage) {
      this.currentPage = 0;
    }

    this.isLoading = true;

    const searchParams = {
      ...this.filters,
      page: this.currentPage,
      size: this.pageSize
    };

    this.ticketAdminService.searchTickets(searchParams).subscribe({
      next: (response: any) => {
        this.tickets = [...response.content];
        this.totalElements = response.totalElements;
        this.totalPages = response.totalPages;
        this.isLoading = false;

        const currentIds = this.tickets.map(t => t.id);
        this.selectedRows.forEach(id => {
          if (resetPage && !currentIds.includes(id)) {
            this.selectedRows.delete(id);
          }
        });

        this.cdr.detectChanges();
      },
      error: (err: string) => {
        this.toastr.error(err);
        this.isLoading = false;
      }
    });
  }

  showDeleteConfirmModal = false;

  triggerDeleteConfirm(): void {
    if (this.selectedRows.size === 0) return;
    this.showDeleteConfirmModal = true;
    this.cdr.detectChanges();
  }

  cancelDelete(): void {
    this.showDeleteConfirmModal = false;
  }

  onPageChange(newPage: number): void {
    if (newPage >= 0 && newPage < this.totalPages) {
      this.currentPage = newPage;
      this.selectedRows.clear();

      if (this.hasActiveFilters()) {
        this.onSearch(false);
      } else {
        this.loadTickets();
      }
    }
  }

  toggleRow(ticketId: number): void {
    this.selectedRows.has(ticketId) ? this.selectedRows.delete(ticketId) : this.selectedRows.add(ticketId);
  }

  toggleAll(): void {
    if (this.isAllSelected()) {
      this.tickets.forEach(t => this.selectedRows.delete(t.id));
    } else {
      this.tickets.forEach(t => this.selectedRows.add(t.id));
    }
  }

  isAllSelected(): boolean {
    return this.tickets.length > 0 && this.selectedRows.size === this.tickets.length;
  }

  onLogout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }

  ngOnDestroy(): void {
    this.userSubscription?.unsubscribe();
  }

}
